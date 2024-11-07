package customer.dao.pch;

import java.util.List;
import java.util.Optional;

import customer.tool.DateTools;
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
import cds.gen.pch.T10EmailSendLog;
import cds.gen.pch.T10EmailSendLog;
import cds.gen.pch.Pch_;

import java.time.Instant;

@Repository
public class PchD010Dao extends Dao {

    // 修改
    private static final Logger logger = LoggerFactory.getLogger(PchD010Dao.class);

    /**
     * 根据
     * 
     * @param po
     * @param dno
     */
    public T10EmailSendLog getByID(String po, int dno) {
        Optional<T10EmailSendLog> result = db.run(
                Select.from(Pch_.T10_EMAIL_SEND_LOG)
                        .where(o -> o.PO_NO().eq(po)
                                .and(o.D_NO().eq(dno))))
                .first(T10EmailSendLog.class);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /**
     * 判断log中po是否有已经回答
     * 
     * @param po
     */
    public T10EmailSendLog getByPo(String po) {
        Optional<T10EmailSendLog> result = db.run(
                Select.from(Pch_.T10_EMAIL_SEND_LOG)
                        .where(o -> o.PO_NO().eq(po)
                                .and(o.TYPE().eq("Y"))))
                .first(T10EmailSendLog.class);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    // 修改PCHD002
    public void update(T10EmailSendLog o) {

        o.setUpTime(getNow());
        o.setUpBy(this.getUserId());

        logger.info("修改PCHD010" + o.getPoNo() + o.getDNo());
        db.run(Update.entity(Pch_.T10_EMAIL_SEND_LOG).data(o));
    }

    // 追加
    // 新建
    public void insert(T10EmailSendLog o) {

        logger.info("=================插入pchd010表号码" + o.getPoNo() + o.getDNo() + "================");
        o.setCdBy(getUserId());
        o.setCdTime(getNow());
        o.setCdDate(DateTools.getLocalDate(o.getCdTime()));
        // o.setCdDateTime(DateTools.getTimeAsString(o.getCdTime()));
        db.run(Insert.into(Pch_.T10_EMAIL_SEND_LOG).entry(o));
    }
    // db.run(Update.entity(Inv_.D001_T).entries(tnoList));
}
