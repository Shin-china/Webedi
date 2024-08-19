package customer.dao.sys;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Select;

import cds.gen.sys.Sys_;
import cds.gen.sys.T12Config;
import customer.dao.common.Dao;

@Repository
public class T12ConfigDao extends Dao {
    // Get SYSTEM Configuration
    public T12Config get(String Code) {
        Optional<T12Config> op = db.run(Select.from(Sys_.T12_CONFIG).where(O -> O.CON_CODE().eq(Code)))
                .first(T12Config.class);
        if (op.isPresent()) {
            return op.get();
        }
        return null;
    }
}
