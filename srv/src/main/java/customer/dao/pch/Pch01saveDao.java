package customer.dao.pch;

// import customer.comm.constant.UmcConstants;
// import customer.dao.comm.Dao;

import java.util.List;
import java.util.Optional;

import customer.comm.tool.DateTools;
import customer.dao.common.Dao;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import org.springframework.stereotype.Repository;

import cds.gen.mst.Mst_;
import cds.gen.pch.PchT02PoD;
import cds.gen.pch.PchT03PoC;
import cds.gen.pch.Pch_;

import java.time.Instant;

@Repository
public class Pch01saveDao extends Dao {

    // /**
    //  * 从数据库中通过Id找到既存的棚帆
    //  * 
    //  * @param matId
    //  * @param locId
    //  * @param plantId
    //  * @param shelf
    //  * @return
    //  */
    // public pch01 getT06ShelfByID(String Id) {
    //     java.util.Optional<T06Shelf> result = db
    //             .run(Select.from(Mst_.T06_SHELF).where(o -> o.ID().eq(Id).and(o.DEL_FLAG().eq("N"))))
    //             .first(T06Shelf.class);
    //     if (result.isPresent()) {
    //         return result.get();
    //     }
    //     return null;
    // }

    /**
     * 从数据库中找到既存的棚帆
     * 
     * @param matId
     * @param locId
     * @param plantId
     * @param shelf
     * @return
     */
    public PchT02PoD getByID(String PO_NO, Integer D_NO) {
        java.util.        Optional<PchT02PoD> result = db.run(
            Select.from(Pch_.PCH_T02_PO_D)
                  .where(o -> o.PO_NO().eq(PO_NO)
                  .and(o.D_NO().eq(D_NO)))
        ).first(PchT02PoD.class);

        if (result.isPresent()) {
            return result.get();
        }
        return null;
    };

    public void  insert(PchT02PoD o ,PchT03PoC o2){
        o.setCdTime(getNow());
        //o.setCdBy(getUserId());
        db.run(Insert.into(Pch_.PCH_T02_PO_D).entry(o));
        db.run(Insert.into(Pch_.PCH_T03_PO_C).entry(o2));
    };

    public Instant getNow() {
		return DateTools.getInstantNow();
	}

    public void update(PchT02PoD t02, PchT03PoC t03) {
        t02.setUpTime(getNow());
        t03.setUpTime(getNow());     
            // o.setUpBy(getUserId());
            db.run(Update.entity(Pch_.PCH_T02_PO_D).entry(t02));
            db.run(Update.entity(Pch_.PCH_T03_PO_C).entry(t03));

    };
}
