package customer.dao.pch;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import cds.gen.pch.Pch_;
import cds.gen.pch.T01PoH;
import cds.gen.pch.T02PoD;
import customer.dao.common.Dao;

@Repository
public class PurchaseDataDao extends Dao {

    public T01PoH getByID(String id) {
        Optional<T01PoH> result = db.run(Select.from(Pch_.T01_PO_H).where(o -> o.PO_NO().eq(id)))
                .first(T01PoH.class);
        if (result.isPresent()) {

            return result.get();

        }
        return null;

    }

    public void modify(T01PoH o) {
        T01PoH isExist = getByID(o.getPoNo());
        if (isExist == null) {
            insert(o);
        } else {
            update(o);
        }
    }

    // Insert
    public void insert(T01PoH o) {
        o.setCdTime(getNow());
        db.run(Insert.into(Pch_.T01_PO_H).entry(o));
    }

    // Update
    public void update(T01PoH o) {
        o.setCdTime(getNow());
        db.run(Update.entity(Pch_.T01_PO_H).entry(o));
    }

    public T02PoD getByID2(String id, Integer dn) {
        Optional<T02PoD> result = db.run(Select.from(Pch_.T02_PO_D)
                .where(o -> o.PO_NO().eq(id)
                        .and(o.D_NO().eq(dn))))
                .first(T02PoD.class);

        if (result.isPresent()) {

            return result.get();

        }
        return null;

    }

    public void modify2(T02PoD o2, Boolean d) {
        T02PoD isExist = getByID2(o2.getPoNo(), o2.getDNo());
        if (isExist == null) {
            insert2(o2, d);
        } else {
            update2(o2, d);
        }
    }

    // Insert2
    public void insert2(T02PoD o2, Boolean d) {
        o2.setPoType("C");

        // 如果是删除标记被打上的话 应该是改成D而不是c创建
        if (d) {

            o2.setPoType("D");

        }

        o2.setStatus("1");
        o2.setCdTime(getNow());
        db.run(Insert.into(Pch_.T02_PO_D).entry(o2));
    }

    // Update2
    public void update2(T02PoD o2, Boolean d) {

        o2.setPoType("U");

        if (d) {

            o2.setPoType("D");

        }

        o2.setStatus("1");
        o2.setCdTime(getNow());
        db.run(Update.entity(Pch_.T02_PO_D).entry(o2));
    }

}
