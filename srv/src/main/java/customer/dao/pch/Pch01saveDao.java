package customer.dao.pch;

import java.util.List;
import java.util.Optional;

import customer.comm.tool.DateTools;
import customer.dao.common.Dao;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

import cds.gen.pch.PchT02PoD;
import cds.gen.pch.PchT03PoC;
import cds.gen.pch.Pch_;

import java.time.Instant;

@Repository
public class Pch01saveDao extends Dao {

    // 从采购订单明细表中取 一条 po dn
    public PchT02PoD getByID(String PO_NO, Integer D_NO) {

        Optional<PchT02PoD> result = db.run(
                Select.from(Pch_.PCH_T02_PO_D)
                        .where(o -> o.PO_NO().eq(PO_NO)
                                .and(o.D_NO().eq(D_NO))))
                .first(PchT02PoD.class);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    };

    // 从采购订单确认表 取 po dn delivery date
    public PchT03PoC getByIDsave(String PO_NO, Integer D_NO, LocalDate DELIVERY_DATE) {

        Optional<PchT03PoC> result = db.run(
                Select.from(Pch_.PCH_T03_PO_C)
                        .where(o -> o.PO_NO().eq(PO_NO)
                                .and(o.D_NO().eq(D_NO))
                                .and(o.DELIVERY_DATE().eq(DELIVERY_DATE))))
                .first(PchT03PoC.class);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    };

    @Transactional
    // 向确认表03 添加数据。
    public boolean insertt03(PchT03PoC o) {

        try {
            db.run(Insert.into(Pch_.PCH_T03_PO_C).entry(o));
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    };

    // 获取当前时间
    public Instant getNow() {

        return DateTools.getInstantNow();
    };

    // 更新确认表03
    public boolean update(PchT03PoC t03) {

        try {
            db.run(Update.entity(Pch_.PCH_T03_PO_C).entry(t03));
            return true;
        } catch (Exception e) {
            return false;
        }
    };

    // 从03表中，获取最大的连番
    public int getSeq(String PO_NO, Integer D_NO) {

        Optional<PchT03PoC> result = db.run(
                Select.from(Pch_.PCH_T03_PO_C)
                        .where(o -> o.PO_NO().eq(PO_NO)
                                .and(o.D_NO().eq(D_NO)))
                        .orderBy(o -> o.SEQ().desc())
                        .limit(1))
                .first(PchT03PoC.class);
        if (result.isPresent()) {
            return result.get().getSeq();
        } else {
            return 0;
        }
    };

    public PchT03PoC getByIDchange(String PO_NO, Integer D_NO) {

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getByIDchange'");

    };

    public Boolean delete(String PO_NO, Integer D_NO, LocalDate DELIVERY_DATE) {

        try {
            db.run(Delete.from(Pch_.PCH_T03_PO_C)
                    .where(o -> o.PO_NO().eq(PO_NO).and(o.D_NO().eq(D_NO)).and(o.DELIVERY_DATE().gt(DELIVERY_DATE))));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
