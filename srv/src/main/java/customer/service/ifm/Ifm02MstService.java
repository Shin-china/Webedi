package customer.service.ifm;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cds.gen.mst.T01SapMat;
import cds.gen.sys.T11IfManager;
import customer.bean.mst.Value;
import customer.bean.mst.SapMstRoot;
import customer.dao.mst.MaterialDataDao;
import customer.dao.sys.IFSManageDao;
import customer.odata.S4OdataTools;

@Component
public class Ifm02MstService {

    @Autowired
    private IFSManageDao ifsManageDao1;

    @Autowired
    private MaterialDataDao MSTDao;

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
                o.setMatUnit(value.getBaseUnit());
                o.setMatType(value.getProductType());
                o.setMatGroup(value.getProductGroup());
                o.setManuCode(value.getManufacturerNumber());
                o.setManuMaterial(value.getProductManufacturerNumber());
                // o.setMatStatus(value.getCrossPlantStatus());
                // o.setMatName(value.getBaseUnit());
                // o.setManuCode(value.getBaseUnit());
                // o.setManuMaterial(value.getBaseUnit());
                MSTDao.modify(o);

            }

        } catch (UnsupportedOperationException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}
