package customer.dao.pch;

import java.util.List;
import java.util.Optional;

import cds.gen.pch.*;
import cds.gen.tableservice.Pch08List_;
import cds.gen.tableservice.PchT07QuotationD;
import cds.gen.tableservice.PchT07QuotationD_;
import com.sap.cds.Result;
import customer.bean.pch.Pch08DetailParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;

import cds.gen.sys.Sys_;
import cds.gen.sys.T08ComOpD;
import customer.dao.common.Dao;
import customer.tool.DateTools;

@Repository
public class PchD008Dao extends Dao {
    // LOG
    private static final Logger logger = LoggerFactory.getLogger(PchD002.class);

    /**
     * 根据h_code查找
     * 
     * @param po
     * @param dno
     */
    public T08ComOpD getByID(String id) {
        Optional<T08ComOpD> result = db.run(
                Select.from(Sys_.T08_COM_OP_D)
                        .where(o -> o.ID().eq(id)))
                .first(T08ComOpD.class);

        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /**
     * 根据h_code查找
     * 
     * @param po
     * @param dno
     */
    public List<T08ComOpD> getByList(String h_code) {
        List<T08ComOpD> listOf = db.run(
                Select.from(Sys_.T08_COM_OP_D)
                        .where(o -> o.H_CODE().eq(h_code)))
                .listOf(T08ComOpD.class);

        return listOf;
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
