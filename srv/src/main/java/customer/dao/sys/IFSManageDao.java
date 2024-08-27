package customer.dao.sys;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Select;

import cds.gen.sys.Sys_;
import cds.gen.sys.T01User;
import cds.gen.sys.T11IfManager;
import customer.dao.common.Dao;

@Repository
public class IFSManageDao extends Dao {

    public T11IfManager getByCode(String string) {
        Optional<T11IfManager> first = db
                .run(Select.from(Sys_.T11_IF_MANAGER).where(o -> o.IF_CODE().eq(string)))
                .first(T11IfManager.class);
        if (first.isPresent()) {
            return first.get();
        }

        return null;
    }

}
