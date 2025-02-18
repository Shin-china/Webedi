package customer.dao.sys;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import java.util.Optional;
import cds.gen.sys.Sys_;
import cds.gen.sys.T02Role;
import customer.dao.common.Dao;

@Repository
public class RoleDao extends Dao{

    public void insertRole(T02Role role) {
        db.run(Insert.into(Sys_.T02_ROLE).entry(role));
    }

    public void updateRole(T02Role role) {
        db.run(Update.entity(Sys_.T02_ROLE).entry(role));
    }

    public T02Role getRoleById(String id) {

        Optional<T02Role> op = db.run(Select.from(Sys_.T02_ROLE).where(o -> o.ID().eq(id))).first(T02Role.class);
        if (op.isPresent()) {
            return op.get();
        }
        return null;

    }
    public T02Role getRoleByRodeCode(String rodeCode) {
        Optional<T02Role> op = db.run(Select.from(Sys_.T02_ROLE).where(o -> o.ROLE_CODE().eq(rodeCode))).first(T02Role.class);
        if (op.isPresent()) {
            return op.get();
        }
        return null;
    }  

       public void deleteRole(String id) {

        db.run(Delete.from(Sys_.T02_ROLE).where(o -> o.ID().eq(id)));

    }
}
