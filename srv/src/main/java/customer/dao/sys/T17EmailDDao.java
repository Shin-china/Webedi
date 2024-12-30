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
import cds.gen.sys.T17EmailD;
import cds.gen.sys.T17EmailD;
import cds.gen.pch.Pch_;
import cds.gen.tableservice.TableService_;
import cds.gen.tableservice.PCHT07QuoItemMax1;

import java.time.Instant;

import customer.bean.com.UmcConstants;

@Repository
public class T17EmailDDao extends Dao {

    private static final Logger logger = LoggerFactory.getLogger(T17EmailDDao.class);

    public T17EmailD getByID(String id) {
        Optional<T17EmailD> result = db.run(
                Select.from(Sys_.T17_EMAIL_D)
                        .where(o -> o.ID().eq(id)))
                .first(T17EmailD.class);

        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    // 追加
    public void insert(T17EmailD o) {

        logger.info("插入SYSt17表code" + o.getEmailAddress() + "================");
        o.setCdBy(getUserId());
        o.setCdTime(getNow());
        if (o.getId() == null) {
            o.setId(UniqueIDTool.getUUID());
        }
        db.run(Insert.into(Sys_.T17_EMAIL_D).entry(o));
    }

    public void update(T17EmailD o) {
        o.setUpTime(getNow());
        o.setUpBy(this.getUserId());

        logger.info("变更SYSt17表code" + o.getEmailAddress() + "================");
        db.run(Update.entity(Sys_.T17_EMAIL_D).data(o));
    }

    // 删除
    public void delete(T17EmailD o) {
        logger.info("删除SYSt17表code" + o.getEmailAddress() + "================");

        db.run(Delete.from(Sys_.T17_EMAIL_D).where(a -> a.ID().eq(o.getId())));

    }

    public void delete() {
        logger.info("删除SYSt17");

        db.run(Delete.from(Sys_.T17_EMAIL_D));
    }
}
