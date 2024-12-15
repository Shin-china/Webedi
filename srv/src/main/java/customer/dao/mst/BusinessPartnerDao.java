package customer.dao.mst;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import cds.gen.mst.T03SapBp;
import cds.gen.mst.Mst_;
import customer.dao.common.Dao;

@Repository
public class BusinessPartnerDao extends Dao {
    public T03SapBp getByID(String id) {
        Optional<T03SapBp> result = db.run(Select.from(Mst_.T03_SAP_BP).where(o -> o.BP_ID().eq(id)))
                .first(T03SapBp.class);
        if (result.isPresent()) {
            return result.get();

        }
        return null;

    }

    // 更新或者创建
    public void modify(T03SapBp o) {
        T03SapBp isExist = getByID(o.getBpId());
        if (isExist == null) {
            insert(o);
        } else {
            update(o);
        }
    }

    // Insert
    public void insert(T03SapBp o) {
        o.setCdTime(getNow());
        o.setCdBy(getUserId());
        db.run(Insert.into(Mst_.T03_SAP_BP).entry(o));
    }

    // Update
    public void update(T03SapBp o) {
        o.setUpTime(getNow());
        o.setUpBy(getUserId());
        db.run(Update.entity(Mst_.T03_SAP_BP).entry(o));
    }

}
