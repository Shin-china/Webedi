package customer.dao.common;

import org.springframework.beans.factory.annotation.Autowired;

import com.sap.cds.services.environment.CdsProperties.Persistence;
import com.sap.cds.services.persistence.PersistenceService;

public class Dao {
    @Autowired
    protected PersistenceService db;

}
