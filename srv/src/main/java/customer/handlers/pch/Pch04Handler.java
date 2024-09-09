// package customer.handlers.pch;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
// import com.sap.cds.services.handler.EventHandler;
// import com.sap.cds.services.handler.annotations.On;
// import com.sap.cds.services.handler.annotations.ServiceName;
// import cds.gen.tableservice.TableService_;
// import cds.gen.MailBody; // 确保导入 MailBody
// import cds.gen.MailJson; // 确保导入 MailJson
// import customer.service.sys.EmailServiceFun;
// import com.google.gson.Gson; // 使用Gson库进行JSON解析
// import java.util.ArrayList;
// import java.util.Collection;
// import java.util.List;

// @Component
// @ServiceName(TableService_.CDS_NAME)
// public class Pch04Handler implements EventHandler {

//     @Autowired
//     private EmailServiceFun emailServiceFun;

//     @On(event = "PCH04_SENDEMAIL")
//     public String sendEmail(String parms) {
//         // 将 JSON 字符串解析为 List<MailParam> 对象
//         List<MailParam> emailParams = parseParams(parms);

//         // 创建 MailJson 的集合
//         Collection<MailJson> mailJsonList = new ArrayList<>();

//         // 遍历每个邮件参数，并创建 MailJson 对象
//         for (MailParam param : emailParams) {
//             MailJson mailJson = MailJson.create(); // 使用静态方法创建 MailJson 对象

//             // 确保使用正确的方法名和参数
//             mailJson.setMailTo(param.getSupplierDescription()); // 设置收件人
//             mailJson.setTemplateId(param.getTemplateId()); // 设置模板ID
//             mailJson.setMailBody(createMailBody(param)); // 设置邮件内容（MailBody）

//             // 添加到邮件列表
//             mailJsonList.add(mailJson);
//         }

//         // 调用邮件发送服务
//         emailServiceFun.sendEmailFun(mailJsonList);

//         // 返回成功消息
//         return "邮件已成功发送。";
//     }

//     // 创建 MailBody 的集合
//     private Collection<MailBody> createMailBody(MailParam param) {
//         Collection<MailBody> bodies = new ArrayList<>();
//         // 根据需要构建 MailBody 对象并添加到集合中
//         MailBody body = MailBody.create();
//         body.setObject("相关内容"); // 根据具体需求设置
//         body.setValue(param.getEmailContent()); // 使用参数内容
//         bodies.add(body);
//         return bodies;
//     }

//     // 解析传入的 JSON 参数为 List<MailParam>
//     private List<MailParam> parseParams(String parms) {
//         Gson gson = new Gson();
//         MailParam[] emailParamsArray = gson.fromJson(parms, MailParam[].class);
//         return List.of(emailParamsArray);
//     }

//     // 定义一个内部类，用于接收 JSON 数据
//     private static class MailParam {
//         private String INV_NO;
//         private String SUPPLIER_DESCRIPTION;
//         private String EMAIL_CONTENT;
//         private String TEMPLATE_ID; // 添加模板ID字段

//         public String getInvNo() {
//             return INV_NO;
//         }

//         public String getSupplierDescription() {
//             return SUPPLIER_DESCRIPTION;
//         }

//         public String getEmailContent() {
//             return EMAIL_CONTENT;
//         }

//         public String getTemplateId() { // 添加获取模板ID的方法
//             return TEMPLATE_ID;
//         }
//     }
// }
