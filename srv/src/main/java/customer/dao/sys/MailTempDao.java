package customer.dao.sys;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.Delete;

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
    public void Update(T11MailTemplate o) {
        db.run(Update.entity(Sys_.T11_MAIL_TEMPLATE).entry(o));
    }

    // 删除
    public void Delete(String id) {
        db.run(Delete.from(Sys_.T11_MAIL_TEMPLATE).where(c -> c.TEMPLATE_ID().eq(id)));
    }

    // 查找是否存在
    public boolean mailTemplateExist(String id) {
        Optional<T11MailTemplate> o = db.run(Select.from(Sys_.T11_MAIL_TEMPLATE).where(c -> c.TEMPLATE_ID().eq(id)))
                .first(T11MailTemplate.class);
        return o.isPresent();
    }
}
