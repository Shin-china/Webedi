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

import cds.gen.pch.T02PoD;
import cds.gen.pch.T03PoC;
import cds.gen.pch.Pch_;

import java.math.BigDecimal;
import java.time.Instant;

@Repository
public class Pch01saveDao extends Dao {

    // 从采购订单明细表中取 一条 po dn
    public T02PoD getByID(String PO_NO, Integer D_NO) {

        Optional<T02PoD> result = db.run(
                Select.from(Pch_.T02_PO_D)
                        .where(o -> o.PO_NO().eq(PO_NO)
                                .and(o.D_NO().eq(D_NO))))
                .first(T02PoD.class);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    };

    // 从采购订单确认表 取 po dn delivery date
    public T03PoC getByIDsave(String PO_NO, Integer D_NO, LocalDate DELIVERY_DATE) {

        Optional<T03PoC> result = db.run(
                Select.from(Pch_.T03_PO_C)
                        .where(o -> o.PO_NO().eq(PO_NO)
                                .and(o.D_NO().eq(D_NO))
                                .and(o.DELIVERY_DATE().eq(DELIVERY_DATE))))
                .first(T03PoC.class);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    };

    @Transactional
    // 向确认表03 添加数据。
    public boolean insertt03(T03PoC o) {

        try {
            db.run(Insert.into(Pch_.T03_PO_C).entry(o));
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
    public boolean update(T03PoC t03) {

        try {
            db.run(Update.entity(Pch_.T03_PO_C).entry(t03));
            return true;
        } catch (Exception e) {
            return false;
        }
    };

    // 从03表中，获取最大的连番
    public int getSeq(String PO_NO, Integer D_NO) {

        Optional<T03PoC> result = db.run(
                Select.from(Pch_.T03_PO_C)
                        .where(o -> o.PO_NO().eq(PO_NO)
                                .and(o.D_NO().eq(D_NO)))
                        .orderBy(o -> o.SEQ().desc())
                        .limit(1))
                .first(T03PoC.class);
        if (result.isPresent()) {
            return result.get().getSeq();
        } else {
            return 0;
        }
    };

    public T03PoC getByIDchange(String PO_NO, Integer D_NO) {

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getByIDchange'");

    };

    public Boolean delete(String PO_NO, Integer D_NO, LocalDate DELIVERY_DATE) {

        try {
            db.run(Delete.from(Pch_.T03_PO_C)
                    .where(o -> o.PO_NO().eq(PO_NO).and(o.D_NO().eq(D_NO)).and(o.DELIVERY_DATE().gt(DELIVERY_DATE))));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean delete_pono(String po_NO, Integer d_NO) {

        try {
            db.run(Delete.from(Pch_.T03_PO_C)
                    .where(o -> o.PO_NO().eq(po_NO)
                            .and(o.D_NO().eq(d_NO))
                            .and(o.RelevantQuantity().isNull().or(o.RelevantQuantity().eq(BigDecimal.ZERO))))); // 检查A字段是否为空
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean update02status(T02PoD t02) {

        try {

            db.run(Update.entity(Pch_.T02_PO_D).entry(t02));

            return true;

        } catch (Exception e) {

            return false;

        }
    }
}
