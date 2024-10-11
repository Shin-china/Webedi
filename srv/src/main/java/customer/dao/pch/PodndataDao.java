package customer.dao.pch;

import com.alibaba.fastjson.serializer.IntegerCodec;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cloud.sdk.datamodel.odata.client.adapter.BigDecimalAdapter;

import cds.gen.pch.PchT01PoH;
import cds.gen.pch.PchT02PoD;
import cds.gen.pch.PchT03PoC;
import cds.gen.pch.Pch_;
import cds.gen.sys.T01User;
import customer.bean.pch.Pch01;
import customer.dao.common.Dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import javax.print.DocFlavor.STRING;

@Repository
public class PodndataDao extends Dao {

    private static final Logger logger = LoggerFactory.getLogger(PodndataDao.class);

    public PchT02PoD getByID(String PO_NO , Integer D_NO) {
        Optional<PchT02PoD> result = db.run(
            Select.from(Pch_.PCH_T02_PO_D)
                  .where(o -> o.PO_NO().eq(PO_NO).and(o.D_NO().eq(D_NO)))
        ).first(PchT02PoD.class);
        
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    //获取 t02 表 po dn 
    public BigDecimal getQuantity(String PO_NO , Integer D_NO) {

        Optional<PchT02PoD> result = db.run(
            Select.from(Pch_.PCH_T02_PO_D)
                  .where(o -> o.PO_NO().eq(PO_NO).and(o.D_NO().eq(D_NO)))
        ).first(PchT02PoD.class);
        
        if (result.isPresent()) {
            
            return result.get().getPoPurQty();

        }else{

            return BigDecimal.ZERO;

        }

    }

    public List<PchT03PoC> getAll() {
        return db.run(Select.from(Pch_.PCH_T03_PO_C)).listOf(PchT03PoC.class);
        
    }

    public void insert(PchT03PoC o) {
        // o.setCdTime(getNow());
        //  o.setCdBy(user.getUserId());
        db.run(Insert.into(Pch_.PCH_T03_PO_C).entry(o));
    }

    public void update(PchT03PoC o) {
        // o.setUpTime(getNow());
        // o.setUpBy(getUserId());
        db.run(Update.entity(Pch_.PCH_T03_PO_C).entry(o));
    }

    public void deleteAll() {
        db.run(Delete.from(Pch_.PCH_T03_PO_C));
    }

}
