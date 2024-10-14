package customer.dao.pch;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import cds.gen.pch.T03PoC;
import cds.gen.pch.Pch_;
import cds.gen.sys.T01User;
import customer.bean.pch.Pch01;
import customer.dao.common.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.print.DocFlavor.STRING;

@Repository
public class DeliverydateDao extends Dao {

    private static final Logger logger = LoggerFactory.getLogger(DeliverydateDao.class);

    public T03PoC getByID(String PO_NO, Integer D_NO, LocalDate deleDate) {
        Optional<T03PoC> result = db.run(
                Select.from(Pch_.T03_PO_C)
                        .where(o -> o.PO_NO().eq(PO_NO)
                                .and(o.D_NO().eq(D_NO))
                                .and(o.DELIVERY_DATE().eq(deleDate))))
                .first(T03PoC.class);

        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public List<T03PoC> getAll() {
        return db.run(Select.from(Pch_.T03_PO_C)).listOf(T03PoC.class);

    }

    public void insert(T03PoC o) {
        // o.setCdTime(getNow());
        // o.setCdBy(user.getUserId());
        db.run(Insert.into(Pch_.T03_PO_C).entry(o));
    }

    public void update(T03PoC o) {
        // o.setUpTime(getNow());
        // o.setUpBy(getUserId());
        db.run(Update.entity(Pch_.T03_PO_C).entry(o));
    }

    public void deleteAll() {
        db.run(Delete.from(Pch_.T03_PO_C));
    }

}
