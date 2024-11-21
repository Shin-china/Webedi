package customer.dao.mst;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import cds.gen.mst.Mst_;
import cds.gen.mst.T03SapBp;
import cds.gen.mst.T05SapBpPurchase;
import customer.dao.common.Dao;

@Repository
public class BPPurchaseDao extends Dao {
    public T05SapBpPurchase getByID(String Suppler, String purchase_org) {
        Optional<T05SapBpPurchase> result = db
                .run(Select.from(Mst_.T05_SAP_BP_PURCHASE)
                        .where(o -> o.SUPPLIER().eq(Suppler).and(o.PURCHASE_ORG().eq(purchase_org))))
                .first(T05SapBpPurchase.class);
        if (result.isPresent()) {
            return result.get();

        }
        return null;
    }

    // 更新或者创建
    public void modify(T05SapBpPurchase o) {
        T05SapBpPurchase isExist = getByID(o.getSupplier(), o.getPurchaseOrg());
        if (isExist == null) {
            insert(o);
        } else {
            update(o);
        }
    }

    // Insert
    public void insert(T05SapBpPurchase o) {
        o.setCdTime(getNow());
        db.run(Insert.into(Mst_.T05_SAP_BP_PURCHASE).entry(o));
    }

    // Update
    public void update(T05SapBpPurchase o) {
        o.setCdTime(getNow());
        db.run(Update.entity(Mst_.T05_SAP_BP_PURCHASE).entry(o));
    }
}
