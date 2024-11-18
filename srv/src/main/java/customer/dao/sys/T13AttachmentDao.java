package customer.dao.sys;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;

import cds.gen.sys.Sys_;
import cds.gen.sys.T13Attachment;
import customer.dao.common.Dao;

@Repository
public class T13AttachmentDao extends Dao {

    // Get Attachment List
    public List<T13Attachment> getAttachmentList(String objectType) {
        return db.run(Select.from(Sys_.T13_ATTACHMENT).where(o -> o.OBJECT_TYPE().eq(objectType)))
                .listOf(T13Attachment.class);
    }

    // Insert Attachment
    public void insertAttachment(T13Attachment attachment) {
        Optional<T13Attachment> getLastVersion = db.run(Select.from(Sys_.T13_ATTACHMENT)
                .where(o -> o.OBJECT().eq(attachment.getObject())).orderBy(o -> o.OBJECT_VERSION().desc()))
                .first(T13Attachment.class);
        if (getLastVersion.isPresent()) {
            attachment.setObjectVersion(getLastVersion.get().getObjectVersion() + 1);
        } else {
            attachment.setObjectVersion(1);
        }

        db.run(Insert.into(Sys_.T13_ATTACHMENT).entry(attachment));
    }

    // Delete Attachment
    public void deleteAttachment(String uuiddString) {
        db.run(Delete.from(Sys_.T13_ATTACHMENT)
                .where(o -> o.ID().eq(uuiddString)));
    }

    // Get Attachment name on S3
    public String getFileName(String uuiddString) {
        Optional<T13Attachment> fileName = db.run(Select.from(Sys_.T13_ATTACHMENT).where(o -> o.ID().eq(uuiddString)))
                .first(T13Attachment.class);
        return fileName.get().getObjectLink();
    }

}
