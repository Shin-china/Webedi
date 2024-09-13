package customer.dao.sys;

import java.util.List;
import org.springframework.stereotype.Repository;
import com.sap.cds.ql.Select;
import cds.gen.sys.Sys_;
import cds.gen.sys.T12Config;
import customer.dao.common.Dao;

@Repository
public class T12ConfigDao extends Dao {
    // Get SYSTEM Configuration
    public List<T12Config> get(String Code) {
        return db.run(Select.from(Sys_.T12_CONFIG).where(O -> O.CON_CODE().contains(Code))).listOf(T12Config.class);
    }
}
