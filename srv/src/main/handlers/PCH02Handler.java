package customer.usapbe.handlers.pch;

import org.springframework.stereotype.Component;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import cds.gen.tableservice.PCH02ConfirmationResquestContext;
import cds.gen.tableservice.TableService_;
import com.sap.cds.services.handler.EventHandler;

@Component
@ServiceName(TableService_.CDS_NAME)
public class PCH02Handler implements EventHandler {

    // 监听 `PCH02_CONFIRMATION_RESQUEST` 事件
    @On(event = "PCH02_CONFIRMATION_RESQUEST")
    public void handleConfirmationRequest(PCH02ConfirmationResquestContext context) {
        // 获取前端传递的参数
        String params = context.getParms();

        // 打印调试信息
        System.out.println("Received params: " + params);

        // TODO: 在这里实现调用 Web Service 的逻辑
        // 目前仅测试从前端到后端的调用是否成功
        String result = "Successfully received params: " + params;

        // 将结果返回给前端
        context.setResult(result);
    }
}
