package customer.service.ifm;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cds.gen.mst.T01SapMat;
import cds.gen.sys.T11IfManager;
import customer.bean.mst.Results;
import customer.bean.mst.SapMstRoot;
import customer.dao.mst.MaterialDataDao;
import customer.dao.sys.IFSManageDao;
import customer.odata.S4OdataTools;

@Component
public class Ifm02MstService {

    @Autowired
    private IFSManageDao ifsManageDao1;

    private MaterialDataDao MSTDao;

    public String tt;

    public void syncMst() {

        T11IfManager interfaceConfig = ifsManageDao1.getByCode("IFM39");
        System.out.println("111");
        try {

            String response = S4OdataTools.get(interfaceConfig, null, null, null);
            SapMstRoot sapMstRoot = JSON.parseObject(response, SapMstRoot.class);

            for (Results results : sapMstRoot.getD().getResults()) {

                T01SapMat o = T01SapMat.create();
                o.setMatId(results.getProduct());
                o.setMatName(results.getProductDescription());
                o.setCdTime(results.getCreationDateTime());
                o.setCdBy(results.getCreatedByUser());
                o.setUpBy(results.getLastChangedByUser());
                o.setUpTime(results.getLastChangeDateTime());

                MSTDao.modify(o);
            }

        } catch (UnsupportedOperationException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

    }

}
