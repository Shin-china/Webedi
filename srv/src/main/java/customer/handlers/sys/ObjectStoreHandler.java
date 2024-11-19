package customer.handlers.sys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.io.ByteStreams;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.AttachmentJson;
import cds.gen.common.Common_;
import cds.gen.common.DeleteS3ObjectContext;
import cds.gen.common.GetS3ListContext;
import cds.gen.common.Pch06BatchImportContext;
import cds.gen.common.Pch06BatchSendingContext;
import cds.gen.common.PchT06QuotationH;
import cds.gen.common.PchT07QuotationD;
import cds.gen.common.S3DownloadAttachmentContext;
import cds.gen.common.S3uploadAttachmentContext;
import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
import cds.gen.sys.T13Attachment;
import customer.bean.com.CommMsg;
import customer.bean.com.UmcConstants;
import customer.comm.tool.StringTool;
import customer.dao.pch.PchD006;
import customer.dao.pch.PchD007;
import customer.dao.sys.DocNoDao;
import customer.dao.sys.T13AttachmentDao;
import customer.service.sys.ObjectStoreService;
import customer.tool.UniqueIDTool;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.S3Object;

@Component
@ServiceName(Common_.CDS_NAME)
public class ObjectStoreHandler implements EventHandler {

    @Autowired
    private ObjectStoreService objectStoreService;
    @Autowired
    private PchD006 PchD006;
    @Autowired
    private PchD007 PchD007;
    @Autowired
    private T13AttachmentDao t13AttachmentDao;
    @Autowired
    DocNoDao docNoDao;

    @On(event = "getS3List")
    public void getObjects(GetS3ListContext context) {
        List<S3Object> s3Objects = objectStoreService.getS3List();
        context.setResult("sucess");
    }

    @On(event = "s3uploadAttachment")
    public void s3UploadAttachment(S3uploadAttachmentContext context) throws IOException {

        Collection<AttachmentJson> attachments = context.getAttachmentJson();
        for (AttachmentJson attachment : attachments) {
            if (attachment.getFileName() != "" && attachment.getValue() != null) {
                String uuidd = UniqueIDTool.getUUID();
                String fieldId = uuidd + "." + attachment.getFileType();
                CommMsg msg = objectStoreService.uploadFile(fieldId, RequestBody
                        .fromBytes(ByteStreams.toByteArray(StringTool.base2InputStream(attachment.getValue()))));
                if (msg.getMsgType().equals(UmcConstants.IF_STATUS_S)) {
                    T13Attachment t13 = T13Attachment.create();
                    t13.setObject(attachment.getObject());
                    t13.setFileName(attachment.getFileName());
                    t13.setFileType(attachment.getFileType());
                    // t13.setObjectType("PCH03");
                    t13.setId(uuidd);
                    t13.setObjectType(attachment.getObjectType());
                    t13.setObjectLink(msg.getMsgTxt());
                    t13AttachmentDao.insertAttachment(t13);
                }

            }
        }

        context.setResult("success");
    }

    @On(event = "deleteS3Object")
    public void deleteS3Object(DeleteS3ObjectContext context) {
        String keyName = context.getKey();
        CommMsg msg = objectStoreService.deleteRes(keyName);

        context.setResult(msg.getMsgTxt());
    }

    @On(event = "s3DownloadAttachment")
    public void s3DownloadAttachment(S3DownloadAttachmentContext context) throws IOException {
        Collection<AttachmentJson> attachments = context.getAttachmentJson();
        ResponseBytes msg = null;

        for (AttachmentJson attachment : attachments) {
            if (attachment.getObject().equals("download")) {
                msg = objectStoreService.downLoadRes(attachment.getValue());
            } else if (attachment.getObject().equals("template")) {
                msg = objectStoreService.downTempRes(attachment.getValue());
            }

        }
        byte[] bytes = msg.asByteArray();
        context.setResult(bytes);
    }

}
