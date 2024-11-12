package customer.dao.pch;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import customer.comm.tool.DateTools;
import customer.dao.common.Dao;

import io.vavr.collection.Seq;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnUpdate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

import cds.gen.pch.T02PoD;
import cds.gen.pch.T03PoC;
import cds.gen.pch.T03PoC_;
import cds.gen.pch.Pch_;
import cds.gen.pch.T04PaymentH;
import cds.gen.pch.T04PaymentH_;

import java.time.Instant;

@Repository
public class PchD004 extends Dao {
    // 修改
    private static final Logger logger = LoggerFactory.getLogger(PchD004.class);

  
    public void updateMap(Map<String, Object> data,Map<String, Object> keys ) {


        CqnUpdate update = Update.entity(Pch_.T04_PAYMENT_H, b -> b.matching(keys)).data(data);
        db.run(update);
    }

}
