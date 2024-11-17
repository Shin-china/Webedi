package customer.handlers.sys;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.excel.write.metadata.fill.FillConfig;

import com.google.common.io.ByteStreams;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.AttachmentJson;
import cds.gen.common.*;
import cds.gen.sys.T13Attachment;
import cds.gen.tableservice.EXCELTESTContext;
import customer.bean.com.CommMsg;
import customer.bean.com.UmcConstants;
import customer.bean.tmpl.test;
import customer.comm.tool.StringTool;
import customer.dao.pch.PchD006;
import customer.dao.pch.PchD007;
import customer.dao.pch.PchD008Dao;
import customer.dao.sys.T13AttachmentDao;
import customer.service.ifm.Ifm01BpService;
import customer.service.sys.ObjectStoreService;
import customer.tool.UniqueIDTool;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.S3Object;
import cds.gen.pch.T07QuotationD;
import cds.gen.pch.T06QuotationH;

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
                    t13.setObjectType("PCH03");
                    t13.setId(uuidd);
                    t13.setObjectType(attachment.getObjectType());
                    t13.setObjectLink(msg.getMsgTxt());
                    t13AttachmentDao.insertAttachment(t13);
                }

            }
        }

        context.setResult("success");
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
