// package customer.handlers.pch;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
// import com.sap.cds.services.handler.EventHandler;
// import com.sap.cds.services.handler.annotations.On;
// import com.sap.cds.services.handler.annotations.ServiceName;
// import cds.gen.common.Common_;
// import cds.gen.tableservice.TableService_;
// import customer.service.sys.EmailServiceFun;

// @Component
// @ServiceName(TableService_.CDS_NAME)  
// public class Pch04Handler implements EventHandler {

//     @Autowired
//     private EmailServiceFun emailServiceFun;

//     @On(event = "PCH04_SENDEMAIL")
//     public String sendEmail(String parms) {
//         // 解析传入的参数，这里假设参数是 JSON 格式的字符串
//         // 您可以根据实际情况进行解析
//         String recipient = extractRecipient(parms); // 从 parms 中提取收件人
//         String subject = "支払通知書発行のお知らせ";
//         String content = createEmailContent(recipient); // 创建邮件内容

//         // 调用邮件服务发送邮件
//         emailServiceFun.sendEmailFun(recipient, subject, content);

//         // 返回结果
//         return "邮件已发送到: " + recipient;
//     }

//     private String extractRecipient(String parms) {
//         // 假设 parms 是 JSON 字符串，解析它以提取收件人信息
//         // 这里您可以使用 JSON 解析库，例如 Jackson 或 Gson
//         // 示例（伪代码）:
//         // JsonObject jsonObject = JsonParser.parseString(parms).getAsJsonObject();
//         // return jsonObject.get("recipient").getAsString();
        
//         // 这里假设直接返回一个固定的地址，您需要根据实际情况实现
//         return "xiaoyue.wang@sh.shin-china.com";
//     }

//     private String createEmailContent(String recipient) {
//         return String.format("（%s）御中\nいつも大変お世話になっております｡\n\n" +
//             "○年△月度の支払通知書がアップロードされましたので、\n" +
//             "以下のURLから、所定のID・Passwordでログインの上、ご確認の程、宜しくお願いいたします。\n\n" +
//             "御社の金額と差異がある場合、○年×月△日までにご連絡ください。\n\n" +
//             "尚､ダウンロード期限がございますので、\n" +
//             "お早めにご確認及びダウンロード頂き、ファイルの保管をお願いいたします。\n\n" +
//             "UWEB（WEB-EDI） URL：*****\n\n" +
//             "以上\n\n" +
//             "ユー・エム・シー・エレクトロニクス　株式会社　購買統括部", recipient);
//     }
// }
