package customer.handlers.pch;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.PCH04SENDEMAILContext;
import cds.gen.tableservice.PCH04EXCELDOWNLOADContext;
import cds.gen.tableservice.TableService_;
import cds.gen.MailBody;
import cds.gen.MailJson;
import customer.bean.tmpl.pch04excel;
import customer.bean.tmpl.test;
import customer.service.sys.EmailServiceFun;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch04Handler implements EventHandler {

    @Autowired
    private EmailServiceFun emailServiceFun;

    @On(event = "PCH04_SENDEMAIL")
    public void sendEmail(PCH04SENDEMAILContext context) {
        // 直接从上下文中获取参数
        String emailJsonParam = (String) context.get("parms"); // 根据上下文对象获取数据
        
        // 解析 JSON 参数为 List<MailParam> 对象
        List<MailParam> emailParams = parseParams(emailJsonParam);

        // 创建 MailJson 的集合
        Collection<MailJson> mailJsonList = new ArrayList<>();

        // 遍历每个邮件参数，并创建 MailJson 对象
        for (MailParam param : emailParams) {
            MailJson mailJson = MailJson.create(); // 使用静态方法创建 MailJson 对象

            // 设置邮件参数
            mailJson.setMailTo(param.getMailTo()); // 设置收件人邮箱
            mailJson.setTemplateId(param.getTemplateId()); // 设置模板ID
            mailJson.setMailBody(createMailBody(param)); // 设置邮件内容（MailBody）

            // 添加到邮件列表
            mailJsonList.add(mailJson);
        }

        // 调用邮件发送服务
        try {
            emailServiceFun.sendEmailFun(mailJsonList);
            // 设置操作结果
            context.setResult(JSON.toJSONString("メール送信に成功しました。"));
        } catch (Exception e) {
            // 处理发送邮件的异常
            context.setResult(JSON.toJSONString("メール送信に失敗しました。エラー: " + e.getMessage()));
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
        Type mailParamListType = new TypeToken<List<MailParam>>() {}.getType();
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

    // // Excel 导出
    // @On(event = "PCH04_EXCELDOWNLOAD")
    // public void exportExcel(PCH04EXCELDOWNLOADContext context) throws IOException {
    //     String content = context.getContent();
    //     byte[] bytes = null;
    //     List<test> dataList = new ArrayList<>();
    //     pch04excel exl = JSON.parseObject(content, pch04excel.class);
    //     pch04excel temp = new pch04excel();
    //     temp.setNO_DETAILS(exl.getNO_DETAILS());
    //     temp.setMAT_ID(exl.getMAT_ID());
    //     temp.setMAT_DESC(exl.getMAT_DESC());
    //     temp.setGR_DATE(exl.getGR_DATE());
    //     temp.setQUANTITY(exl.getQUANTITY());
    //     temp.setUNIT_PRICE_IN_YEN(exl.getUNIT_PRICE_IN_YEN());
    //     temp.setBASE_AMOUNT_EXCLUDING_TAX(exl.getBASE_AMOUNT_EXCLUDING_TAX());
    //     temp.setTAX_RATE(exl.getTAX_RATE());
    //     temp.setPO_TRACK_NO(exl.getPO_TRACK_NO());
    //     temp.setINV_BASE_DATE(exl.getINV_BASE_DATE());
    //     temp.setTOTAL_PRICE_AMOUNT_8(exl.getTOTAL_PRICE_AMOUNT_8());
    //     temp.setCONSUMPTION_TAX_8(exl.getCONSUMPTION_TAX_8());
    //     temp.setTOTAL_PAYMENT_AMOUNT_8_END(exl.getTOTAL_PAYMENT_AMOUNT_8_END());
    //     temp.setTOTAL_PRICE_AMOUNT_10(exl.getTOTAL_PRICE_AMOUNT_10());
    //     temp.setCONSUMPTION_TAX_10(exl.getCONSUMPTION_TAX_10());
    //     temp.setTOTAL_PAYMENT_AMOUNT_10_END(exl.getTOTAL_PAYMENT_AMOUNT_10_END());
    //     temp.setNON_APPLICABLE_AMOUNT(exl.getNON_APPLICABLE_AMOUNT());
    //     temp.setTOTAL_PAYMENT_AMOUNT_FINAL(exl.getTOTAL_PAYMENT_AMOUNT_FINAL());
    //     temp.setINV_MONTH_FORMATTED(exl.getINV_MONTH_FORMATTED());
    //     temp.setSUPPLIER_DESCRIPTION(exl.getSUPPLIER_DESCRIPTION());
    //     temp.setLOG_NO(exl.getLOG_NO());
    //     temp.setCompany_Name(exl.getCompany_Name());
    //     temp.setCURRENT_DAY(exl.getCURRENT_DAY());
    //     // dataList.add(temp);

    //     // 获取模板文件
    //     InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/test.xlsx");

    //     // Excel写入数据
    //     ExcelWriter excelWriter = null;
    //     try {
    //         ByteArrayOutputStream os = new ByteArrayOutputStream();
    //         excelWriter = EasyExcel.write(os).withTemplate(inputStream).inMemory(true).build();
    //         WriteSheet writeSheet = EasyExcel.writerSheet().build();

    //         // 填充完后需要换行
    //         FillConfig fileConfig = FillConfig.builder().forceNewRow(true).build();
    //         // 写入数据
    //         // excelWriter.write(os, writeSheet)
    //         excelWriter.fill(dataList, fileConfig, writeSheet);
    //         // 重新计算公式
    //         Workbook workbook = excelWriter.writeContext().writeWorkbookHolder().getWorkbook();
    //         // 调用
    //         workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();

    //         excelWriter.finish();

    //         // 获取byte字节、
    //         bytes = os.toByteArray();
    //     } catch (Exception e) {

    //     } finally {
    //         if (excelWriter != null) {
    //             excelWriter.finish();
    //         }
    //     }

    //     context.setResult(bytes);
    // }
}
