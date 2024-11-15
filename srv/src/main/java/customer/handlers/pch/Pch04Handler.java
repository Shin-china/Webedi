package customer.handlers.pch;
import java.io.ByteArrayOutputStream;
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

import customer.bean.tmpl.test;
import customer.service.sys.EmailServiceFun;
import customer.bean.tmpl.Pch04;
import customer.bean.tmpl.Pch04List;
import customer.bean.tmpl.Pch05;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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

     // Excel 导出测试
  @On(event = "PCH04_EXCELDOWNLOAD")
  public void exportExcel(PCH04EXCELDOWNLOADContext context) throws IOException {
    // String content = context.getContent();
    byte[] bytes = null;
    Pch04List dataList = JSON.parseObject(context.getParms(),Pch04List.class);

    // 检查 dataList 是否为空，并且确保它的 list 字段存在
    if (dataList != null && dataList.getList() != null) {
      // 遍历 Pch04List 中的每一项（每个 item 为 Pch04List 的一个记录）
      for (Pch04 item : dataList.getList()) {

            String grDateString = item.getGR_DATE();
            String invBaseDateString = item.getINV_BASE_DATE();

            // 如果 GR_DATE 存在，解析并格式化它
            if (grDateString != null && !grDateString.isEmpty()) {
                try {
                    // 使用 OffsetDateTime 解析带时区的日期字符串
                    OffsetDateTime grDate = OffsetDateTime.parse(grDateString); // 解析为 OffsetDateTime
                    String formattedGrDate = grDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")); // 格式化为 yyyy/MM/dd
                    item.setGR_DATE(formattedGrDate); // 设置格式化后的日期
                } catch (Exception e) {
                    e.printStackTrace(); // 如果解析失败，打印错误信息
                }
            }

            // 如果 INV_BASE_DATE 存在，解析并格式化它
            if (invBaseDateString != null && !invBaseDateString.isEmpty()) {
                try {
                    // 使用 OffsetDateTime 解析带时区的日期字符串
                    OffsetDateTime invBaseDate = OffsetDateTime.parse(invBaseDateString); // 解析为 OffsetDateTime
                    String formattedInvBaseDate = invBaseDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd")); // 格式化为 yyyy/MM/dd
                    item.setINV_BASE_DATE(formattedInvBaseDate); // 设置格式化后的日期
                } catch (Exception e) {
                    e.printStackTrace(); // 如果解析失败，打印错误信息
            }
      }
  }

}

    // 获取模板文件
    InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/支払通知照会.xlsx");

    // Excel写入数据
    ExcelWriter excelWriter = null;
    try {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      excelWriter = EasyExcel.write(os).withTemplate(inputStream).inMemory(true).build();
      WriteSheet writeSheet = EasyExcel.writerSheet().build();

      //map组合数据
      HashMap<String, String> otherData = new HashMap<>();
      otherData.put("TOTAL_PRICE_AMOUNT_8",dataList.getList().get(0).getTOTAL_PRICE_AMOUNT_8());
      otherData.put("CONSUMPTION_TAX_8",dataList.getList().get(0).getCONSUMPTION_TAX_8());
      otherData.put("TOTAL_PAYMENT_AMOUNT_8_END",dataList.getList().get(0).getTOTAL_PAYMENT_AMOUNT_8_END());
      otherData.put("TOTAL_PRICE_AMOUNT_10",dataList.getList().get(0).getTOTAL_PRICE_AMOUNT_10());
      otherData.put("CONSUMPTION_TAX_10",dataList.getList().get(0).getCONSUMPTION_TAX_10());
      otherData.put("TOTAL_PAYMENT_AMOUNT_10_END",dataList.getList().get(0).getTOTAL_PAYMENT_AMOUNT_10_END());
      otherData.put("NON_APPLICABLE_AMOUNT",dataList.getList().get(0).getNON_APPLICABLE_AMOUNT());
      otherData.put("TOTAL_PAYMENT_AMOUNT_FINAL",dataList.getList().get(0).getTOTAL_PAYMENT_AMOUNT_FINAL());
      otherData.put("INV_MONTH_FORMATTED",dataList.getList().get(0).getINV_MONTH_FORMATTED());
      otherData.put("SUPPLIER_DESCRIPTION",dataList.getList().get(0).getSUPPLIER_DESCRIPTION());
      otherData.put("LOG_NO",dataList.getList().get(0).getLOG_NO());
      otherData.put("Company_Name",dataList.getList().get(0).getCompany_Name());
      otherData.put("CURRENT_DAY",dataList.getList().get(0).getCURRENT_DAY());

      // 填充完后需要换行
      FillConfig fileConfig = FillConfig.builder().forceNewRow(true).build();
      // 写入数据
      // excelWriter.write(os, writeSheet)
      excelWriter.fill(dataList.getList(), fileConfig, writeSheet);
      // 重新计算公式
      Workbook workbook = excelWriter.writeContext().writeWorkbookHolder().getWorkbook();
      // 调用
      workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
        // 填充
        excelWriter.fill(otherData, writeSheet);
      excelWriter.finish();

      // 获取byte字节、
      bytes = os.toByteArray();
    } catch (Exception e) {

    } finally {
      if (excelWriter != null) {
        excelWriter.finish();
      }
    }

    context.setResult(bytes);
  }
}
