package customer.dao.common;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sap.cds.services.environment.CdsProperties.Persistence;
import com.sap.cds.services.persistence.PersistenceService;

import customer.comm.tool.DateTools;

@Repository
public class Dao {
    @Autowired
    protected PersistenceService db;

    // 得到时间时间戳
    public Instant getNow() {
        return DateTools.getInstantNow();
    }

}
