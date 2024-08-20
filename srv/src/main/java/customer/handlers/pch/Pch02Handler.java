package customer.handlers.pch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.tableservice.TableService_;

import cds.gen.tableservice.PCH02ConfirmationREQUESTContext;
import com.sap.cds.services.handler.EventHandler;

@Component
@ServiceName(TableService_.CDS_NAME)
public class Pch02Handler implements EventHandler {

    // 监听 `PCH02_CONFIRMATION_REQUEST` 事件
    @On(event = "PCH02_CONFIRMATION_REQUEST")
    public void handleConfirmationRequest(PCH02ConfirmationREQUESTContext context) {
        // 返回成功消息
        System.out.println("aabb");
        context.setResult("Success");
    }
}
