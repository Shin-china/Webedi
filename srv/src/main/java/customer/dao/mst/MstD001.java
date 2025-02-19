package customer.dao.mst;

import java.util.List;
import java.util.Optional;

import customer.comm.tool.DateTools;
import customer.dao.common.Dao;
import io.vavr.collection.Seq;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

import cds.gen.mst.Mst_;
import cds.gen.mst.T01SapMat;
import cds.gen.mst.T03SapBp;

import java.time.Instant;

@Repository
public class MstD001 extends Dao {

    private static final Logger logger = LoggerFactory.getLogger(MstD001.class);

    public T01SapMat getByID(String MATERIAL_NUMBER) {
        Optional<T01SapMat> result = db.run(
                Select.from(Mst_.T01_SAP_MAT)
                        .where(o -> o.MAT_ID().eq(MATERIAL_NUMBER)))
                .first(T01SapMat.class);

        if (result.isPresent()) {
            return result.get();
            
        }
        return null;
    }

    public T01SapMat getCustMat(String CUST_MATERIAL) {
        Optional<T01SapMat> result = db.run(
                Select.from(Mst_.T01_SAP_MAT)
                        .where(o -> o.CUST_MATERIAL().eq(CUST_MATERIAL)))
                .first(T01SapMat.class);

        if (result.isPresent()) {
            return result.get();
            
        }
        return null;
    }

}
