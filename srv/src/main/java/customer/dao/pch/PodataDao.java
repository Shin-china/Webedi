package customer.dao.pch;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import cds.gen.pch.T01PoH;
import cds.gen.pch.T03PoC;
import cds.gen.pch.Pch_;
import cds.gen.sys.T01User;
import customer.bean.pch.Pch01;
import customer.dao.common.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.print.DocFlavor.STRING;

@Repository
public class PodataDao extends Dao {

    private static final Logger logger = LoggerFactory.getLogger(PodataDao.class);

    public T01PoH getByID(String PO_NO) {
        Optional<T01PoH> result = db.run(
                Select.from(Pch_.T01_PO_H)
                        .where(o -> o.PO_NO().eq(PO_NO)
                                .and(o.DEL_FLAG().isNotNull())))
                .first(T01PoH.class);

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

    public BigDecimal getByID2(int lastdn, String lastpo) {

        // 从数据库中选择 PO_NO 和 D_NO 匹配的记录，且 RelevantQuantity 不为空
        List<T03PoC> results = db.run(Select.from(Pch_.T03_PO_C)
                .where(po -> po.PO_NO().eq(lastpo).and(po.D_NO().eq(lastdn)).and(po.RelevantQuantity().isNotNull())))
                .listOf(T03PoC.class);

        // 对所有符合条件的记录的 RelevantQuantity 进行求和
        return results.stream()
                .map(T03PoC::getRelevantQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    public LocalDate getLastDeliveryDate(int lastdn, String lastpo) {

        // 从数据库中选择 PO_NO 和 D_NO 匹配的记录，且 RelevantQuantity 不为空
        List<T03PoC> results = db.run(Select.from(Pch_.T03_PO_C)
                .where(po -> po.PO_NO().eq(lastpo).and(po.D_NO().eq(lastdn)).and(po.RelevantQuantity().isNotNull())))
                .listOf(T03PoC.class);

        // 找到最大 DELIVERY_DATE
        return results.stream()
                .map(T03PoC::getDeliveryDate) // 提取 DELIVERY_DATE
                .max(LocalDate::compareTo) // 获取最大的 DELIVERY_DATE
                .orElse(null); // 如果没有符合条件的记录，则返回 null

    }

}
