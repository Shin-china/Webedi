package customer.dao.pch;

import cds.gen.pch.Pch_;
import cds.gen.pch.T06QuotationH;
import cds.gen.pch.T07QuotationD;
import com.sap.cds.ql.Update;
import customer.dao.common.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class Pch08Dao extends Dao {

    // 修改
    private static final Logger logger = LoggerFactory.getLogger(Pch08Dao.class);

    // 修改t06, t07
    public void updatePch08(T06QuotationH h, T07QuotationD d) {
        d.setUpTime(getNow());
        d.setUpBy(this.getUserId());
        logger.info("修改T07QuotationD" + d.getQuoNumber() + "行号：" + d.getQuoItem());
        db.run(Update.entity(Pch_.T07_QUOTATION_D).data(d));

        h.setUpTime(getNow());
        h.setUpBy(this.getUserId());

        logger.info("修改T06QuotationH" + h.getQuoNumber());
        db.run(Update.entity(Pch_.T07_QUOTATION_D).data(d));
        db.run(Update.entity(Pch_.T06_QUOTATION_H).data(h));

    }

    // db.run(Update.entity(Inv_.D001_T).entries(tnoList));
}
