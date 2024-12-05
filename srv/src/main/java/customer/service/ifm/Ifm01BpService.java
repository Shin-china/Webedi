package customer.service.ifm;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cds.gen.mst.T03SapBp;
import cds.gen.sys.T11IfManager;
import customer.bean.bp.Results;
import customer.bean.bp.SapBpRoot;
import customer.bean.bp.To_AddressIndependentFax;
import customer.bean.bp.To_BusinessPartnerAddress;
import customer.bean.bp.To_BusinessPartnerTax;
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
            for (Results results : sapBpRoot.getD().getResults()) {
                T03SapBp o = T03SapBp.create();
                // o.setBpId(results.getSupplier());

                o.setBpId(results.getBusinessPartner());

                o.setBpName1(results.getOrganizationBPName1());
                o.setBpName2(results.getOrganizationBPName2());
                o.setBpName3(results.getOrganizationBPName3());
                o.setBpName4(results.getOrganizationBPName4());

                // Fax
                for (To_AddressIndependentFax fax : results.getTo_AddressIndependentFax().getResults()) {
                    o.setFax(fax.getInternationalFaxNumber());
                }
                // Address
                for (To_BusinessPartnerAddress addr : results.getTo_BusinessPartnerAddress().getResults()) {
                    o.setPostcode(addr.getPostalCode());
                    o.setRegions(addr.getStreetName());
                    o.setPlaceName(addr.getCityName());
                }

                // Tax Number
                for (To_BusinessPartnerTax tax : results.getTo_BusinessPartnerTax().getResults()) {
                    o.setLogNo(tax.getBPTaxNumber());
                }

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
