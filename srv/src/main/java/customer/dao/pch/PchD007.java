package customer.dao.pch;

import java.util.List;
import java.util.Optional;

import customer.dao.common.Dao;
import customer.tool.DateTools;
import customer.tool.UniqueIDTool;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import cds.gen.pch.T07QuotationD;
import cds.gen.pch.Pch_;

@Repository
public class PchD007 extends Dao {

    private static final Logger logger = LoggerFactory.getLogger(PchD007.class);

    public T07QuotationD getByID(String MATERIAL_NUMBER) {
        Optional<T07QuotationD> result = db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.MATERIAL_NUMBER().eq(MATERIAL_NUMBER)))
                .first(T07QuotationD.class);

        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    // 追加
    public void insert(T07QuotationD o) {

        logger.info("=================插入pchd07表号码" + "================");
        o.setCdBy(getUserId());
        o.setCdTime(getNow());
        if (o.getId() == null) {
            o.setId(UniqueIDTool.getUUID());
        }
        o.setCdDate(DateTools.getLocalDate(o.getCdTime()));
        o.setCdDateTime(DateTools.getTimeAsString(o.getCdTime()));

        db.run(Insert.into(Pch_.T07_QUOTATION_D).entry(o));
    }

    public List<T07QuotationD> getList(String quoNum) {
        return db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.QUO_NUMBER().eq(quoNum)))
                .listOf(T07QuotationD.class);
    }

    public void update(T07QuotationD o) {
        db.run(Update.entity(Pch_.T07_QUOTATION_D).entry(o));
    }

    public T07QuotationD getByT07Id(String t07Id) {
        Optional<T07QuotationD> result = db.run(
                Select.from(Pch_.T07_QUOTATION_D)
                        .where(o -> o.ID().eq(t07Id)))
                .first(T07QuotationD.class);

        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

}
