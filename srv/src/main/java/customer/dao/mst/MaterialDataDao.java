package customer.dao.mst;

import java.util.HashMap;
import java.util.Optional;

import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner.noneDSA;
import org.springframework.stereotype.Repository;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;

import cds.gen.mst.T01SapMat;
import cds.gen.mst.T06MatPlant;
import cds.gen.sys.Sys_;
import cds.gen.sys.T08ComOpD;
import cds.gen.mst.Mst_;
import customer.bean.mst.Value;
import customer.bean.mst._ProductDescription;
import customer.bean.mst._ProductPlant;
import customer.comm.constant.ConfigConstants;
import customer.comm.odata.OdateValueTool;
import customer.dao.common.Dao;

import java.text.ParseException;

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
        o.setCdBy(getUserId());
        db.run(Insert.into(Mst_.T01_SAP_MAT).entry(o));
    }

    // Update
    public void update(T01SapMat o) {
        o.setUpTime(getNow());
        o.setUpBy(getUserId());
        db.run(Update.entity(Mst_.T01_SAP_MAT).entry(o));
    }

    public void modifyt06(T06MatPlant o2) {
        T06MatPlant isExist = getById2(o2.getMatId(), o2.getPlantId());
        if (isExist == null) {
            insert2(o2);
        } else {
            update2(o2);
        }

    }

    private T06MatPlant getById2(String matId, String plantId) {

        Optional<T06MatPlant> result = db
                .run(Select.from(Mst_.T06_MAT_PLANT)
                        .where(o -> o.MAT_ID().eq(matId).and(o.PLANT_ID().eq(plantId))))

                .first(T06MatPlant.class);
        if (result.isPresent()) {

            return result.get();

        }

        return null;

    }

    private void update2(T06MatPlant o2) {

        o2.setUpTime(getNow());
        o2.setUpBy(getUserId());

        db.run(Update.entity(Mst_.T06_MAT_PLANT).entry(o2));
    }

    private void insert2(T06MatPlant o2) {

        o2.setCdTime(getNow());
        o2.setCdBy(getUserId());
        db.run(Insert.into(Mst_.T06_MAT_PLANT).entry(o2));

    }

    public String getDnameByHcodeDcode(String h_code, String d_code) {

        Optional<T08ComOpD> listOf = db.run(
                Select.from(Sys_.T08_COM_OP_D)
                        .where(o -> o.H_CODE().eq(h_code).and(o.D_CODE().eq(d_code))))

                .first(T08ComOpD.class);

        if (listOf.isPresent()) {

            return listOf.get().getDName();

        }

        return d_code;

    }

    public T01SapMat S4Mat_2_locl_Mat(Value s4, T01SapMat mat) throws ParseException { // 本地化的物料信息

        String matId = s4.getProduct();

        mat.setMatId(matId);

        String dbMat = s4.getBaseUnit();

        if (dbMat != null) {

            String dbMatUser = getDnameByHcodeDcode("S4_UNIT_TEC_2_USER", dbMat);

            mat.setMatUnit(dbMatUser);

        }

        mat.setMatUnit(s4.getBaseUnit());
        mat.setMatType(s4.getProductType());
        mat.setMatGroup(s4.getProductGroup());
        mat.setManuCode(s4.getManufacturerNumber());

        mat.setSapCdBy(s4.getCreatedByUser());
        mat.setSapCdTime(OdateValueTool.ISO8601ToInstant(s4.getCreationDateTime()));
        mat.setSapUpBy(s4.getLastChangedByUser());
        mat.setSapUpTime(OdateValueTool.ISO8601ToInstant(s4.getLastChangeDateTime()));

        mat.setMatName("CDS中取品目名称多语言!");

        if (s4.get_ProductDescription() != null) { // 物料多语言
            int index = 0;
            for (_ProductDescription a : s4.get_ProductDescription()) {

                if (index == 0
                        ||
                        ConfigConstants.USER_LANG_CODE.equals(OdateValueTool.getLocaleCode(a.getLanguage()))) { // 任意给一个。或者直接给日文
                    mat.setMatName(a.getProductDescription());
                }
                index++;
            }
        }

        if (s4.get_ProductPlant() != null) { // 工厂信息
            for (_ProductPlant a2 : s4.get_ProductPlant()) {

                T06MatPlant o2 = T06MatPlant.create();
                o2.setMatId(matId);
                o2.setPlantId(a2.getPlant());

                if (s4.get_ProductPlant().get(0).getProductIsCriticalPrt()) {
                    o2.setImpComp("X");
                } else {
                    o2.setImpComp(" ");
                }

                modifyt06(o2);
            }
        }

        return mat;
    }

}
