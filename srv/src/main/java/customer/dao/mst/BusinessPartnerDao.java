package customer.dao.mst;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import cds.gen.mst.MstT03SapBp;
import cds.gen.mst.Mst_;
import customer.dao.common.Dao;

@Repository
public class BusinessPartnerDao extends Dao {
    public MstT03SapBp getByID(String id) {
        Optional<MstT03SapBp> result = db.run(Select.from(Mst_.MST_T03_SAP_BP).where(o -> o.BP_ID().eq(id)))
                .first(MstT03SapBp.class);
        if (result.isPresent()) {
            return result.get();

        }
        return null;

    }

    // 更新或者创建
    public void modify(MstT03SapBp o) {
        MstT03SapBp isExist = getByID(o.getBpId());
        if (isExist == null) {
            insert(o);
        } else {
            update(o);
        }
    }

    // Insert
    public void insert(MstT03SapBp o) {
        o.setCdTime(getNow());
        db.run(Insert.into(Mst_.MST_T03_SAP_BP).entry(o));
    }

    // Update
    public void update(MstT03SapBp o) {
        o.setCdTime(getNow());
        db.run(Update.entity(Mst_.MST_T03_SAP_BP).entry(o));
    }
}
