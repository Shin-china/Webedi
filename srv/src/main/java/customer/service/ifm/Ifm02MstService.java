package customer.service.ifm;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cds.gen.mst.T01SapMat;
import cds.gen.mst.T06MatPlant;
import cds.gen.sys.T06DocNo;
import cds.gen.sys.T11IfManager;
import customer.bean.mst.Value;
import customer.bean.mst.SapMstRoot;
import customer.dao.mst.MaterialDataDao;
import customer.dao.sys.IFSManageDao;
import customer.dao.sys.SysD008Dao;
import customer.odata.S4OdataTools;

@Component
public class Ifm02MstService {

    @Autowired
    private IFSManageDao ifsManageDao1;

    @Autowired
    private MaterialDataDao MSTDao;

    @Autowired
    private SysD008Dao sysD008Dao;

    public String tt;

    public void syncMst() {

        T11IfManager interfaceConfig = ifsManageDao1.getByCode("IFM39");

        try {

            String response = S4OdataTools.get(interfaceConfig, null, null, null);
            SapMstRoot sapMstRoot = JSON.parseObject(response, SapMstRoot.class);

            for (Value value : sapMstRoot.getValue()) {

                T01SapMat o = T01SapMat.create();

                o.setMatId(value.getProduct());
                o.setCdBy(value.getCreatedByUser());
                o.setUpBy(value.getLastChangedByUser());

                String dbMat = value.getBaseUnit();

                if (dbMat != null) {

                    String dbMatUser = sysD008Dao.getDnameByHcode("S4_UNIT_TEC_2_USER");

                    o.setMatUnit(dbMatUser);

                }

                // o.setMatUnit(value.getBaseUnit());
                o.setMatType(value.getProductType());
                o.setMatGroup(value.getProductGroup());
                o.setManuCode(value.getManufacturerNumber());
                o.setManuMaterial(value.getProductManufacturerNumber());

                o.setMatName(value.get_ProductDescription().get(0).getProductDescription());
                // o.setMatStatus(value.getCrossPlantStatus());
                // o.setMatName(value.getBaseUnit());
                // o.setManuCode(value.getBaseUnit());
                // o.setManuMaterial(value.getBaseUnit());
                o.setCustMaterial(value.getYY1_CUSTOMERMATERIAL_PRD());
                MSTDao.modify(o);

                T06MatPlant o2 = T06MatPlant.create();

                value.get_ProductPlant().get(0).getProductIsCriticalPrt();

                o2.setMatId(value.get_ProductPlant().get(0).getProduct());
                o2.setPlantId(value.get_ProductPlant().get(0).getPlant());
                if (value.get_ProductPlant().get(0).getProductIsCriticalPrt()) {
                    o2.setImpComp("X");
                } else {
                    o2.setImpComp(" ");
                }

                MSTDao.modifyt06(o2);

            }

        } catch (Exception e) {

            System.out.println(e.getMessage());

        }

    }

}
