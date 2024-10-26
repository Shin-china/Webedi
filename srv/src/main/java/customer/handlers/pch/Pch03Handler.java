package customer.handlers.pch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.Select;
import com.sap.cds.reflect.CdsService;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.After;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.PCH03QUERENContext;
import cds.gen.tableservice.PCH03SENDEMAILContext;
import cds.gen.tableservice.PCH04SENDEMAILContext;
import cds.gen.tableservice.PchT03PoItemPrint;
import cds.gen.tableservice.PchT03PoItemPrint_;
import cds.gen.tableservice.TableService_;
import cds.gen.MailBody;
import cds.gen.MailJson;
import customer.comm.tool.StringTool;
import customer.service.pch.PchService;
import customer.service.sys.EmailServiceFun;
import customer.tool.UWebConstants;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch03Handler implements EventHandler {

    @Autowired
    private EmailServiceFun emailServiceFun;
    @Autowired
    PchService pchService;

    /**
     * 
     * @param context
     */
    @On(event = "PCH03_QUEREN")
    public void queRn(PCH03QUERENContext context) {
        // 直接从上下文中获取参数
        JSONArray jsonArray = JSONArray.parseArray(context.getParms());
        // 根据传入的po和po明细修改po明细状态
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String po = jsonObject.getString("po");
            String dNo = jsonObject.getString("dNo");

            pchService.setPoStu(po, dNo);
        }

        context.setResult("success");
    }

    /**
     * 
     * 打印前数据处理
     * 
     */
    @After(entity = PchT03PoItemPrint_.CDS_NAME, event = "READ")
    public void beforeReadD03PDF(CdsReadEventContext context, Stream<PchT03PoItemPrint> pchd03List) {
        pchd03List.forEach(pchd03 -> {
            // 获取po
            String po = pchd03.getPoNo();
            // 获取明细编号
            String dNo = pchd03.getDNo() + "";
            // 设置前置零
            pchd03.setPodno(po + StringTool.leftPadWithZeros(dNo, 5));

            // 设置税的四个
            BigDecimal unitPrice = pchd03.getUnitPrice();
            BigDecimal delPrice = pchd03.getDelPrice();
            BigDecimal poPurQty = pchd03.getPoPurQty();
            // 货币除了日元5其余2
            String currency = pchd03.getCurrency();
            // 单价
            BigDecimal prc = BigDecimal.ZERO;
            if (UWebConstants.JPY.equals(currency)) {
                prc = delPrice.divide(unitPrice, 5, RoundingMode.HALF_UP);
            } else {
                prc = delPrice.divide(unitPrice, 2, RoundingMode.HALF_UP);
            }
            // 税抜額
            BigDecimal exclusive_tax_amount = poPurQty.multiply(prc);
            // 税额
            // 获取税率
            BigDecimal sl = pchService.getSysD006("税率");
            BigDecimal tax_amount = exclusive_tax_amount.multiply(sl);
            // 税込額
            BigDecimal inclusive_tax_amount = tax_amount.add(exclusive_tax_amount);

            pchd03.setOrderUnitPrice(prc.toString());
            pchd03.setExclusiveTaxAmount(exclusive_tax_amount.toString());
            pchd03.setTaxAmount(tax_amount.toString());
            pchd03.setInclusiveTaxAmount(inclusive_tax_amount.toString());

            // 设置検査合区分
            if (!StringTool.isEmpty(pchd03.getImpComp())) {
                pchd03.setCheckOk("受入検査あり");
            }
            pchd03.setQrCode(pchService.getQrCode(pchd03));

        });
    }

    @On(event = PCH03SENDEMAILContext.CDS_NAME)
    public void sendEmail(PCH03SENDEMAILContext context) {
        // 直接从上下文中获取参数
        String emailJsonParam = (String) context.get("parms"); // 根据上下文对象获取数据

        ArrayList<MailJson> mailJsonList = new ArrayList<>();
        // MailJso

        // 调用邮件发送服务
        try {
            emailServiceFun.sendEmailFun(mailJsonList);
            // 设置操作结果
            context.setResult(JSON.toJSONString("メール送信に成功しました。"));
        } catch (Exception e) {
            // 处理发送邮件的异常

        }
    }

    // 创建 MailBody 的集合
    private Collection<MailBody> createMailBody(MailParam param) {
        Collection<MailBody> bodies = new ArrayList<>();
        MailBody body = MailBody.create();
        body.setObject("content"); // 根据具体需求设置
        body.setValue(param.getEmailContent()); // 使用参数内容
        bodies.add(body);
        return bodies;
    }

    // 解析传入的 JSON 参数为 List<MailParam>
    private List<MailParam> parseParams(String parms) {
        Gson gson = new Gson();
        Type mailParamListType = new TypeToken<List<MailParam>>() {
        }.getType();
        return gson.fromJson(parms, mailParamListType);
    }

    // 定义一个内部类，用于接收 JSON 数据
    private static class MailParam {
        private String INV_NO;
        private String SUPPLIER_DESCRIPTION;
        private String EMAIL_CONTENT;
        private String TEMPLATE_ID; // 添加模板ID字段
        private String MAIL_TO; // 添加收件人邮箱字段

        public String getInvNo() {
            return INV_NO;
        }

        public String getSupplierDescription() {
            return SUPPLIER_DESCRIPTION;
        }

        public String getEmailContent() {
            return EMAIL_CONTENT;
        }

        public String getTemplateId() {
            return TEMPLATE_ID;
        }

        public String getMailTo() {
            return MAIL_TO;
        }
    }
}
