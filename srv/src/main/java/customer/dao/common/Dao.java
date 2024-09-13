package customer.dao.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sap.cds.services.environment.CdsProperties.Persistence;
import com.sap.cds.services.persistence.PersistenceService;

@Repository
public class Dao {
    @Autowired
    protected PersistenceService db;

}
