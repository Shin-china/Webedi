package customer.dao.mst;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import cds.gen.mst.T01SapMat;
import cds.gen.mst.Mst_;
import customer.dao.common.Dao;

@Repository
public class MaterialDataDao extends Dao {

    public T01SapMat getByID(String id) {
        Optional<T01SapMat> result = db.run(Select.from(Mst_.T01_SAP_MAT).where(o -> o.MAT_ID().eq(id)))
                .first(T01SapMat.class);
        if (result.isPresent()) {

            return result.get();

        }
        return null;

    }

    public void modify(T01SapMat o) {
        T01SapMat isExist = getByID(o.getMatId());
        if (isExist == null) {
            insert(o);
        } else {
            update(o);
        }
    }

    // Insert
    public void insert(T01SapMat o) {
        o.setCdTime(getNow());
        db.run(Insert.into(Mst_.T01_SAP_MAT).entry(o));
    }

    // Update
    public void update(T01SapMat o) {
        o.setCdTime(getNow());
        db.run(Update.entity(Mst_.T01_SAP_MAT).entry(o));
    }

}
