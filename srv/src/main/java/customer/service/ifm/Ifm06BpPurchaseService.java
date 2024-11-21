package customer.service.ifm;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.alibaba.fastjson.JSON;

import cds.gen.mst.T05SapBpPurchase;
import cds.gen.sys.T11IfManager;
import customer.bean.bp.Results;
import customer.bean.bp.SapBpRoot;
import customer.dao.mst.BPPurchaseDao;
import customer.dao.sys.IFSManageDao;
import customer.odata.S4OdataTools;

@Repository
public class Ifm06BpPurchaseService {
    @Autowired
    private IFSManageDao ifsManageDao;

    @Autowired
    private BPPurchaseDao bpPurchaseDao;

    public void syncBPPurchase() {
        T11IfManager interfaceConfig = ifsManageDao.getByCode("IFM02");
        try {

            String response = S4OdataTools.get(interfaceConfig, null, null, null);
            SapBpRoot sapBpRoot = JSON.parseObject(response, SapBpRoot.class);
            for (Results results : sapBpRoot.getD().getResults()) {
                T05SapBpPurchase o = T05SapBpPurchase.create();
                o.setSupplier(results.getSupplier());
                o.setPurchaseOrg(results.getPurchasingOrganization());
                o.setZabc(results.getSupplierABCClassificationCode());

                bpPurchaseDao.modify(o);
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
