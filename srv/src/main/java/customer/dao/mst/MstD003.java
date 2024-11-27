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

import cds.gen.mst.T03SapBp;
import cds.gen.mst.Mst_;
import cds.gen.mst.T01SapMat;
import cds.gen.mst.T03SapBp;

import java.time.Instant;

@Repository
public class MstD003 extends Dao {

    private static final Logger logger = LoggerFactory.getLogger(MstD003.class);

    public T03SapBp getByID(String BP_NUMBER) {
        Optional<T03SapBp> result = db.run(
                Select.from(Mst_.T03_SAP_BP)
                        .where(o -> o.BP_ID().eq(BP_NUMBER)))
                .first(T03SapBp.class);

        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public T03SapBp getBySearch(String search) {
        List<T03SapBp> listOf = db.run(
                Select.from(Mst_.T03_SAP_BP)
                        .where(o -> o.SEARCH2().eq(search)))
                .listOf(T03SapBp.class);

        if (listOf.size() > 0) {
            return listOf.get(0);
        }
        return null;
    }
}
