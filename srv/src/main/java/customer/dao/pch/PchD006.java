package customer.dao.pch;

import java.util.List;
import java.util.Optional;

import customer.dao.common.Dao;
import customer.tool.DateTools;
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

import cds.gen.pch.T06QuotationH;
import cds.gen.pch.Pch_;
import cds.gen.tableservice.TableService_;
import cds.gen.tableservice.PCHT07QuoItemMax1;

import java.time.Instant;

@Repository
public class PchD006 extends Dao {

    private static final Logger logger = LoggerFactory.getLogger(PchD006.class);

    public T06QuotationH getByID(String PLANT_ID) {
        Optional<T06QuotationH> result = db.run(
                Select.from(Pch_.T06_QUOTATION_H)
                        .where(o -> o.PLANT_ID().eq(PLANT_ID)))
                .first(T06QuotationH.class);

        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    // 追加
    public void insert(T06QuotationH o) {

        logger.info("=================插入pchd06表号码" + "================");
        o.setCdBy(getUserId());
        o.setCdTime(getNow());

        // o.setCdDate(DateTools.getLocalDate(o.getCdTime()));
        // o.setCdDateTime(DateTools.getTimeAsString(o.getCdTime()));

        db.run(Insert.into(Pch_.T06_QUOTATION_H).entry(o));
    }

    // dao层获取传入的QUO_NUMBER所有明细以及头表
    public T06QuotationH get(String quoNumber) {
        Optional<T06QuotationH> first = db
                .run(Select.from(Pch_.T06_QUOTATION_H).columns(o -> o._all(), o -> o.TO_ITEMS().expand())
                        .where(o -> o.QUO_NUMBER().eq(quoNumber)))
                .first(T06QuotationH.class);

        if (first.isPresent()) {
            return first.get();
        }
        return null;
    }

    /**
     * 获取最大的明细行
     * 
     * @param po
     * @param pod
     */
    public PCHT07QuoItemMax1 getVer(String PLANT_ID, String MATERIAL_NUMBER) {

        Optional<PCHT07QuoItemMax1> first = db
                .run(Select.from(TableService_.PCHT07_QUO_ITEM_MAX1)
                        .where(o -> o.PLANT_ID().eq(PLANT_ID).and(o.MATERIAL_NUMBER().eq(MATERIAL_NUMBER))))
                .first(PCHT07QuoItemMax1.class);
        if (first.isPresent()) {
            return (first.get());
        }
        return null;
    }

    public PCHT07QuoItemMax1 getQuoNumberMax() {

        Optional<PCHT07QuoItemMax1> first = db
                .run(Select.from(TableService_.PCHT07_QUO_ITEM_MAX1)
                        .orderBy(o -> o.QUO_NUMBER_MAX().desc())
                        .limit(1))
                .first(PCHT07QuoItemMax1.class);
        if (first.isPresent()) {
            return (first.get());
        }
        return null;
    }

}
