package customer.dao.pch;

import java.util.List;
import java.util.Optional;

import customer.comm.tool.DateTools;
import customer.dao.common.Dao;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.persistence.PersistenceService;
import com.sap.cds.services.request.UserInfo;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cds.gen.pch.T02PoD;
import cds.gen.pch.T02PoD_;
import cds.gen.pch.T03PoC;
import cds.gen.pch.T03PoC_;
import cds.gen.pch.Pch_;

import java.time.Instant;

@Repository
public class PchD002 extends Dao {

    // 修改
    private static final Logger logger = LoggerFactory.getLogger(PchD002.class);

    /**
     * 根据删除po和dno删除03表
     * 
     * @param po
     * @param dno
     */
    public T02PoD getByID(String po, int dno) {
        Optional<T02PoD> result = db.run(
                Select.from(Pch_.T02_PO_D)
                        .where(o -> o.PO_NO().eq(po)
                                .and(o.D_NO().eq(dno))))
                .first(T02PoD.class);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /**
     * 根据删除po
     * 
     * @param po
     * @param dno
     */
    public List<T02PoD> getByPo(String po) {
        List<T02PoD> listOf = db.run(
                Select.from(Pch_.T02_PO_D)
                        .where(o -> o.PO_NO().eq(po)))
                .listOf(T02PoD.class);

        return listOf;
    }

    // 修改PCHD002
    public void updateD002(T02PoD o) {

        o.setUpTime(getNow());
        o.setUpBy(this.getUserId());

        logger.info("修改PCHD002" + o.getPoNo() + o.getDNo());
        db.run(Update.entity(Pch_.T02_PO_D).data(o));
    }

    // db.run(Update.entity(Inv_.D001_T).entries(tnoList));
}
