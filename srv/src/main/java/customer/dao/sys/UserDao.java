package customer.dao.sys;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import cds.gen.sys.Sys_;
import cds.gen.sys.T01User;
import cds.gen.tableservice.SysT01User_;
import customer.dao.common.Dao;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

@Repository
public class UserDao extends Dao {

    public String insert(T01User o) {
        Optional<T01User> first = db.run(Insert.into(Sys_.T01_USER).entry(o)).first(T01User.class);
        return first.get().getId();
    }
}
