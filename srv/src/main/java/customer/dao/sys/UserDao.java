package customer.dao.sys;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import cds.gen.sys.Sys_;
import cds.gen.sys.T01User;
import cds.gen.sys.T04User2Role;
import cds.gen.tableservice.SysT01User_;
import customer.dao.common.Dao;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

@Repository
public class UserDao extends Dao {

    public String insert(T01User o) {
        Optional<T01User> first = db.run(Insert.into(Sys_.T01_USER).entry(o)).first(T01User.class);
        return first.get().getId();
    }

    public void update(T01User o) {
        db.run(Update.entity(Sys_.T01_USER).entry(o));

    }

    // 删除User Role
    public void deleteRole(String id) {
        db.run(Delete.from(Sys_.T01_USER).where(c -> c.USER_ID().eq(id)));
    }

    // 创建User Role
    public void Insert(T04User2Role O) {

    }

    // 根据User ID 获取 UUIDD
    public T01User getById(String id) {
        Optional<T01User> first = db.run(Select.from(Sys_.T01_USER).where(c -> c.USER_ID().eq(id)))
                .first(T01User.class);
        if (first.isPresent()) {
            return first.get();
        }

        return null;
    }

    // 删除User ID
    public void deleteUserID(String id) {
        db.run(Delete.from(Sys_.T01_USER).where(c -> c.USER_ID().eq(id)));
    }
}
