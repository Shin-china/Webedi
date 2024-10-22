package customer.dao.sys;

import java.time.Instant;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;

import cds.gen.mst.Mst_;
import cds.gen.sys.Sys_;
import cds.gen.sys.T09User2Plant;
import cds.gen.sys.T14User2Bp;
import customer.dao.common.Dao;

@Repository
public class User2BpDao extends Dao {
    // 根据用户ID删除
    public void deleteByUserId(String userId) {
        db.run(Delete.from(Sys_.T14_USER2_BP).where(o -> o.USER_ID().eq(userId)));
    }

    // 插入用户BP
    public void insertUser2Bp(T14User2Bp o) {
        o.setUpBy(o.getUserId());
        o.setUpTime(Instant.now());
        db.run(Insert.into(Sys_.T14_USER2_BP).entry(o));
    }
}
