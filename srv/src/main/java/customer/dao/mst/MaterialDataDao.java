package customer.dao.mst;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import cds.gen.mst.MstT01SapMat;
import cds.gen.mst.Mst_;
import customer.dao.common.Dao;


@Repository
public class MaterialDataDao extends Dao {

    public MstT01SapMat getByID(String id) {
        Optional<MstT01SapMat> result = db.run(Select.from(Mst_.MST_T01_SAP_MAT).where(o -> o.MAT_ID().eq(id)))
                .first(MstT01SapMat.class);
        if (result.isPresent()) {

            return result.get() ;

        }
        return null;

    }

    public void modify(MstT01SapMat o) {
        MstT01SapMat isExist = getByID(o.getMatId());
        if (isExist == null) {
            insert(o);
        } else {
            update(o);
        }
    }

    // Insert
    public void insert(MstT01SapMat o) {
        o.setCdTime(getNow());
        db.run(Insert.into(Mst_.MST_T01_SAP_MAT).entry(o));
    }

    // Update
    public void update(MstT01SapMat o) {
        o.setCdTime(getNow());
        db.run(Update.entity(Mst_.MST_T01_SAP_MAT).entry(o));
    }

}
