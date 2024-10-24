package customer.dao.pch;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;

import cds.gen.pch.Pch_;
import cds.gen.pch.T03PoC;
import cds.gen.pch.T07QuotationD;
import cds.gen.pch.T08Upload;
import customer.dao.common.Dao;
import customer.tool.DateTools;

@Repository
public class PchD008Dao extends Dao {
    // LOG
    private static final Logger logger = LoggerFactory.getLogger(PchD002.class);

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

    // 追加
    // 新建现品票
    public void insert(T08Upload o) {

        logger.info("=================插入T08Upload");
        o.setCdBy(getUserId());
        o.setCdTime(getNow());

        o.setCdDate(DateTools.getLocalDate(o.getCdTime()));
        o.setCdDateTime(DateTools.getTimeAsString(o.getCdTime()));
        db.run(Insert.into(Pch_.T08_UPLOAD).entry(o));
    }

}
