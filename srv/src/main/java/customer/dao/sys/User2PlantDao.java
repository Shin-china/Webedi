package customer.dao.sys;

import java.time.Instant;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;

import cds.gen.sys.Sys_;
import cds.gen.sys.T09User2Plant;
import customer.dao.common.Dao;

@Repository
public class User2PlantDao extends Dao {
    // 根据用户ID删除
    public void deleteByUserId(String userId) {
        db.run(Delete.from(Sys_.T09_USER2_PLANT).where(o -> o.USER_ID().eq(userId)));
    }

    // 插入用户工厂
    public void insertUser2Plant(T09User2Plant o) {
        o.setUpBy(o.getUserId());
        o.setUpTime(Instant.now());
        db.run(Insert.into(Sys_.T09_USER2_PLANT).entry(o));
    }
}
