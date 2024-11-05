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
import cds.gen.pch.T10Upload;
import cds.gen.sys.T08ComOpD;
import customer.bean.com.UmcConstants;
import customer.comm.tool.StringTool;
import customer.dao.pch.PchD008Dao;
import customer.dao.pch.PchD010Dao;
import customer.service.pch.PchService;
import customer.service.sys.EmailServiceFun;
import customer.tool.DateTools;
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

    @Autowired
    PchD010Dao pchD010;
    @Autowired
    PchD008Dao pchD008Dao;

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
            // 确认后写log表
            pchService.setT09LogData(jsonObject);

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

        Boolean[] isPrint = new Boolean[1];
        isPrint[0] = false;
        pchd03List.forEach(pchd03 -> {
            // 获取po
            String po = pchd03.getPoNo();
            // 获取明细编号
            String dNo = pchd03.getDNo() + "";
            // 设置前置零
            pchd03.setPodno(po + StringTool.leftPadWithZeros(dNo, 5));
            // 单价
            BigDecimal prc = BigDecimal.ZERO;
            // 设置税的四个
            BigDecimal unitPrice = pchd03.getUnitPrice();
            BigDecimal delPrice = pchd03.getDelPrice();
            BigDecimal poPurQty = pchd03.getPoPurQty();
            // 货币除了日元5其余2
            String currency = pchd03.getCurrency();
            if (unitPrice != null && delPrice != null && poPurQty != null) {
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
            }

            // 设置検査合区分
            if (!StringTool.isEmpty(pchd03.getImpComp())) {
                pchd03.setCheckOk("受入検査あり");
            }
            pchd03.setQrCode(pchService.getQrCode(pchd03));

            // 复制用作显示
            pchd03.setCop1(pchd03.getPodno());
            pchd03.setCop2(pchd03.getPoPurQty() == null ? "" : pchd03.getPoPurQty().toString());
            pchd03.setCop3(pchd03.getPodno());
            pchd03.setCop4(pchd03.getPodno());
            pchd03.setCop5(pchd03.getSupplierMat());
            pchd03.setCop6(pchd03.getMatId());
            pchd03.setCop7(pchd03.getStorage());
            pchd03.setCop8(pchd03.getCheckOk());
            pchd03.setCop9(pchd03.getBpName1());
            pchd03.setCop10(pchd03.getStorage());
            pchd03.setCop11(pchd03.getPodno());
            pchd03.setCop12(pchd03.getSupplierMat());
            pchd03.setCop13(pchd03.getMatId());

            pchd03.setCop14(pchd03.getPodno());
            // pchd03.setCop15(pchd03.getPodno());
            pchd03.setCop16(pchd03.getPodno());
            pchd03.setCop17(pchd03.getCheckOk());
            pchd03.setCop17(pchd03.getCheckOk());

            // 公司固定值取出

            pchd03.setZws1(pchd03.getPodno() + "\n" + pchd03.getCdBy());
            pchd03.setZws2(pchd03.getSupplier() + "\n" + pchd03.getMatId());
            pchd03.setZws3(pchd03.getManuCode());
            pchd03.setZws4(pchd03.getCop2() + "\n" + pchd03.getStorage());
            pchd03.setZws5(pchd03.getPoPurUnit() + "\n" + pchd03.getMemo());
            pchd03.setZws6(prc.toString());
            pchd03.setZws7(pchd03.getCop2());
            pchd03.setZws8(pchd03.getCurrency());
            pchd03.setZws9(DateTools.getCurrentDateString(pchd03.getPoDDate()));

            pchd03.setDate1(DateTools.getCurrentDateString());
            pchd03.setDate2(DateTools.getCurrentDateString());
            pchd03.setDate3(DateTools.getCurrentDateString());

            List<T08ComOpD> byList = pchD008Dao.getByList(UmcConstants.C_INFO);
            for (T08ComOpD t08ComOpD : byList) {
                if (UmcConstants.C_INFO.equals(t08ComOpD.getDName())) {
                    pchd03.setBpName12(t08ComOpD.getValue01());
                }
                if (UmcConstants.C_INFO_POSTCODE.equals(t08ComOpD.getDName())) {
                    pchd03.setPostcode2(t08ComOpD.getValue01());
                }
                if (UmcConstants.C_INFO_REGIONS.equals(t08ComOpD.getDName())) {
                    pchd03.setRegions2(t08ComOpD.getValue01());
                }
                if (UmcConstants.C_INFO_FAX.equals(t08ComOpD.getDName())) {
                    pchd03.setTel2(t08ComOpD.getValue01());
                }
                if (UmcConstants.C_INFO_TEL.equals(t08ComOpD.getDName())) {
                    pchd03.setFax2(t08ComOpD.getValue01());
                }

            }

            // 设置注文书状态
            //
            if (!"D".equals(pchd03.getPoType())) {
                isPrint[0] = true;
            }

        });

        pchd03List.forEach(pchd03 -> {
            if (isPrint[0]) {
                T10Upload byPo = pchD010.getByPo(pchd03.getPoNo());
                if (byPo == null) {
                    pchd03.setType(UmcConstants.ZWS_TYPE_1);
                } else {
                    pchd03.setType(UmcConstants.ZWS_TYPE_2);
                }
            } else {
                pchd03.setType(UmcConstants.ZWS_TYPE_3);
            }

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
