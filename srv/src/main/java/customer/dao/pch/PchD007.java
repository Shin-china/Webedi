package customer.dao.pch;

import java.util.List;
import java.util.Optional;

import customer.dao.common.Dao;
import customer.tool.DateTools;
import io.vavr.collection.Seq;
import software.amazon.awssdk.services.kms.endpoints.internal.Value.Int;

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
import cds.gen.pch.T07QuotationD;
import cds.gen.pch.Pch_;

import java.time.Instant;

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

        // o.setCdDate(DateTools.getLocalDate(o.getCdTime()));
        // o.setCdDateTime(DateTools.getTimeAsString(o.getCdTime()));

        db.run(Insert.into(Pch_.T07_QUOTATION_D).entry(o));
    }

    public List<T07QuotationD> getList(String quoNum) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getList'");
    }

}
