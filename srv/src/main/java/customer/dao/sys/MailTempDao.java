package customer.dao.sys;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;

import cds.gen.sys.Sys_;
import cds.gen.sys.T11MailTemplate;
import customer.dao.common.Dao;

@Repository
public class MailTempDao extends Dao {
    // 创建
    public String Insert(T11MailTemplate o) {
        db.run(Insert.into(Sys_.T11_MAIL_TEMPLATE).entry(o)).first(T11MailTemplate.class);
        return "success";
    }
    // 修改

}
