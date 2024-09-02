package customer.dao.sys;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Select;

import cds.gen.sys.Sys_;
import cds.gen.sys.T13Attachment;
import customer.dao.common.Dao;

@Repository
public class T13AttachmentDao extends Dao {

    // Get Attachment List
    public List<T13Attachment> getAttachmentList() {
        return db.run(Select.from(Sys_.T12_CONFIG).where(null))
    }

}
