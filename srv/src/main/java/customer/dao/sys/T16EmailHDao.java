package customer.dao.sys;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

import customer.dao.common.Dao;
import customer.tool.DateTools;
import customer.tool.UniqueIDTool;
import io.vavr.collection.Seq;

import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.ql.cqn.CqnUpdate;

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
import cds.gen.sys.Sys_;
import cds.gen.sys.T16EmailH;
import cds.gen.pch.Pch_;
import cds.gen.tableservice.TableService_;
import cds.gen.tableservice.PCHT07QuoItemMax1;

import java.time.Instant;

import customer.bean.com.UmcConstants;

@Repository
public class T16EmailHDao extends Dao {

    private static final Logger logger = LoggerFactory.getLogger(T16EmailHDao.class);

    public T16EmailH getByID(String id) {
        Optional<T16EmailH> result = db.run(
                Select.from(Sys_.T16_EMAIL_H)
                        .where(o -> o.ID().eq(id)))
                .first(T16EmailH.class);

        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    // 追加
    public void insert(T16EmailH o) {

        logger.info("插入T16EmailH表code" + o.getHCode() + "================");
        o.setCdBy(getUserId());
        o.setCdTime(getNow());
        if (o.getId() == null) {
            o.setId(UniqueIDTool.getUUID());
        }
        db.run(Insert.into(Sys_.T16_EMAIL_H).entry(o));
    }

    public void update(T16EmailH o) {
        o.setUpTime(getNow());
        o.setUpBy(this.getUserId());

        logger.info("变更T16EmailH表code" + o.getHCode() + "================");
        db.run(Update.entity(Sys_.T16_EMAIL_H).data(o));
    }

    // 删除
    public void delete(T16EmailH o) {
        logger.info("删除T16EmailH表code" + o.getHCode() + "================");

        db.run(Delete.from(Sys_.T16_EMAIL_H).where(a -> a.ID().eq(o.getId())));

    }

    public void delete() {
        logger.info("删除T16EmailH表");

        db.run(Delete.from(Sys_.T16_EMAIL_H));

    }
}
