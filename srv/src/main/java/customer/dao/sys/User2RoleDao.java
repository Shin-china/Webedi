package customer.dao.sys;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;

import cds.gen.sys.Sys_;
import cds.gen.sys.T04User2Role;
import customer.dao.common.Dao;

@Repository
public class User2RoleDao extends Dao {
    // 删除 用户关联Role数据
    public void deleteUser2Role(String userId) {
        db.run(Delete.from(Sys_.T04_USER2_ROLE).where(c -> c.USER_ID().eq(userId)));
    }

    // 插入 用户关联Role数据
    public void insertUser2Role(T04User2Role o) {
        o.setUpBy(o.getUserId());
        o.setUpTime(Instant.now());
        db.run(Insert.into(Sys_.T04_USER2_ROLE).entry(o));
    }
}
