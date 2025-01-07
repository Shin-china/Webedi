package customer.dao.sys;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;

import cds.gen.sys.Sys_;
import cds.gen.sys.T05Role2Auth;
import customer.dao.common.Dao;

@Repository
public class Role2AuthDao  extends Dao {
    public void deleteByRoleId(String roleId) {

        db.run(Delete.from(Sys_.T05_ROLE2_AUTH).where(o -> o.ROLE_ID().eq(roleId)));

    }

    public void insertRole2Auth(T05Role2Auth t) {
        t.setUpBy(this.getUserId());
        t.setUpTime(getNow());
        db.run(Insert.into(Sys_.T05_ROLE2_AUTH).entry(t));
    }

    public void addRole2Auth(String roleId, List<String> authIdList) {
        if (authIdList.size() > 0) {
            List<T05Role2Auth> list = new ArrayList<>();
            for (String authId : authIdList) {
                T05Role2Auth role2Auth = T05Role2Auth.create();
                role2Auth.setId(UUID.randomUUID().toString());
                role2Auth.setAuthId(authId);
                role2Auth.setRoleId(roleId);
                list.add(role2Auth);
            }
            db.run(Insert.into(Sys_.T05_ROLE2_AUTH).entries(list));
        }

    }
}
