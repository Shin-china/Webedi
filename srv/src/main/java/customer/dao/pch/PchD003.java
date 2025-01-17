package customer.dao.pch;

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

import cds.gen.pch.T02PoD;
import cds.gen.pch.T03PoC;
import cds.gen.pch.T03PoC_;
import cds.gen.pch.Pch_;

import java.math.BigDecimal;
import java.time.Instant;

@Repository
public class PchD003 extends Dao {
    // 修改
    private static final Logger logger = LoggerFactory.getLogger(PchD003.class);

    // 追加
    // 新建现品票
    public void insertD003(T03PoC o) {

        logger.info("=================插入pchd03表号码" + o.getPoNo() + o.getDNo() + "================");
        o.setCdBy(getUserId());
        o.setCdTime(getNow());
        db.run(Insert.into(Pch_.T03_PO_C).entry(o));
    }

    /**
     * 根据删除po和dno删除03表
     * 
     * @param po
     * @param dno
     */
    public void deleteD002ByPoDno(String po, int dno) {
        Delete<T03PoC_> delete = Delete.from(Pch_.T03_PO_C);
        // delete.where(o ->
        // o.PO_NO().eq(po).and(o.D_NO().eq(dno).and(o.RelevantQuantity().isNull())));
        delete.where(o -> o.PO_NO().eq(po).and(o.D_NO().eq(dno).and(o.RelevantQuantity().isNull().or(o.RelevantQuantity().eq(BigDecimal.ZERO)))));
        db.run(delete);
    }

    /**
     * 根据id查找
     * 
     * @param po
     * @param dno
     */
    public T03PoC getByID(String po, int dno, int seq) {
        Optional<T03PoC> result = db.run(
                Select.from(Pch_.T03_PO_C)
                        .where(o -> o.PO_NO().eq(po)
                                .and(o.D_NO().eq(dno)).and(o.SEQ().eq(seq))))
                .first(T03PoC.class);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    // 修改PCHD003
    public void updateD003(T03PoC o) {

        o.setUpTime(getNow());
        o.setUpBy(this.getUserId());

        logger.info("修改PCHD003" + o.getPoNo() + o.getDNo() + o.getSeq());
        db.run(Update.entity(Pch_.T03_PO_C).data(o));
    }
}
