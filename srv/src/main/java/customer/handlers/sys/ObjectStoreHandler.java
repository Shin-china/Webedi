package customer.handlers.sys;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.io.ByteStreams;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.AttachmentJson;
import cds.gen.common.*;
import cds.gen.sys.T13Attachment;
import customer.bean.com.CommMsg;
import customer.bean.com.UmcConstants;
import customer.comm.tool.StringTool;
import customer.dao.sys.T13AttachmentDao;
import customer.service.ifm.Ifm01BpService;
import customer.service.sys.ObjectStoreService;
import customer.tool.UniqueIDTool;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.S3Object;

@Component
@ServiceName(Common_.CDS_NAME)
public class ObjectStoreHandler implements EventHandler {

    @Autowired
    private ObjectStoreService objectStoreService;

    @Autowired
    private T13AttachmentDao t13AttachmentDao;

    @On(event = "getS3List")
    public void getObjects(GetS3ListContext context) {
        List<S3Object> s3Objects = objectStoreService.getS3List();
        context.setResult("sucess");
    }

    @On(event = "s3Attachment")
    public void s3Attachment(S3AttachmentContext context) throws IOException {

        Collection<AttachmentJson> attachments = context.getAttachmentJson();
        for (AttachmentJson attachment : attachments) {
            if (attachment.getFileName() != "" && attachment.getValue() != null) {
                String fieldId = UniqueIDTool.getUUID() + "." + attachment.getFileType();
                CommMsg msg = objectStoreService.uploadFile(fieldId, RequestBody
                        .fromBytes(ByteStreams.toByteArray(StringTool.base2InputStream(attachment.getValue()))));
                if (msg.getMsgType().equals(UmcConstants.IF_STATUS_S)) {
                    T13Attachment t13 = T13Attachment.create();
                    t13.setObject(fieldId);
                }

            }
        }

        context.setResult("success");
    }

}
