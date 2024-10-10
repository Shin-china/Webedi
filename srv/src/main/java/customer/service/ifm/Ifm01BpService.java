package customer.service.ifm;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cds.gen.mst.MstT03SapBp;
import cds.gen.sys.T11IfManager;
import customer.bean.bp.Results;
import customer.bean.bp.SapBpRoot;
import customer.dao.mst.BusinessPartnerDao;
import customer.dao.sys.IFSManageDao;
import customer.odata.S4OdataTools;

@Component
public class Ifm01BpService {
    @Autowired
    private IFSManageDao ifsManageDao;

    @Autowired
    private BusinessPartnerDao BPDao;

    public void syncBP() {
        T11IfManager interfaceConfig = ifsManageDao.getByCode("IFM01");
        try {
            String response = S4OdataTools.get(interfaceConfig, null, null, null);
            SapBpRoot sapBpRoot = JSON.parseObject(response, SapBpRoot.class);
            MstT03SapBp o = MstT03SapBp.create();
            for (Results results : sapBpRoot.getD().getResults()) {
                o.setBpId(results.getSupplier());
                o.setBpName1(results.getOrganizationBPName1());
                o.setBpName2(results.getOrganizationBPName2());
                o.setBpName3(results.getOrganizationBPName3());
                o.setBpName4(results.getOrganizationBPName4());
                o.setBpType("SUPP");
                BPDao.modify(o);
            }

        } catch (UnsupportedOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
