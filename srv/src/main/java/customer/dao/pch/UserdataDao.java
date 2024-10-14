package customer.dao.pch;

import java.security.PublicKey;
import java.util.List;
import java.util.Optional;

import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.services.persistence.PersistenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cds.gen.pch.PchT02PoD;
import cds.gen.sys.Sys_;
import cds.gen.tableservice.Pch01Auth1;
import cds.gen.tableservice.Pch01Auth1_;
import cds.gen.tableservice.SysT09User2Plant;
import cds.gen.tableservice.TableService_;
import customer.dao.common.Dao;

import com.sap.cds.Row;

@Repository
public class UserdataDao extends Dao {

    private static final Logger logger = LoggerFactory.getLogger(UserdataDao.class);

    @Autowired
    private PersistenceService db;

    public Pch01Auth1 getUSER_DATA() {
        Optional<Pch01Auth1> op = db.run(Select.from(TableService_.PCH01_AUTH1))
                .first(Pch01Auth1.class);
        if (op.isPresent()) {
            return op.get();
        }
        return null;
    };

    public SysT09User2Plant getT09_PLANT() {

        Optional<SysT09User2Plant> op = db.run(Select.from(TableService_.PCH01_PLANT_CHECK))
                .first(SysT09User2Plant.class);
        if (op.isPresent()) {
            return op.get();
        }
        return null;
    }

}

// public SysT09User2Plant getAUTH_CHECK(String type){

// if ("1".equals(type)) {

// Optional<SysT09User2Plant> op =
// db.run(Select.from(TableService_.PCH01_PLANT_CHECK);

// if (op.isPresent()) {
// return (SysT09User2Plant) op.get().get("ACT_QTY");
// }
// }
// }

// // else if ("2".equals(type)) {
// // return null;
// // }
// }
// }
