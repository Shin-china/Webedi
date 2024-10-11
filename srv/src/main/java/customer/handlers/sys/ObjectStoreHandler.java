package customer.handlers.sys;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import cds.gen.common.*;
import customer.service.ifm.Ifm01BpService;
import customer.service.sys.ObjectStoreService;
import software.amazon.awssdk.services.s3.model.S3Object;

@Component
@ServiceName(Common_.CDS_NAME)
public class ObjectStoreHandler implements EventHandler {

    @Autowired
    private ObjectStoreService objectStoreService;

    @On(event = "getS3List")
    public void getObjects(GetS3ListContext context) {
        // End
        List<S3Object> s3Objects = objectStoreService.getS3List();
        context.setResult("sucess");
    }
}
