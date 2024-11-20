package customer.service.ifm;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;

import cds.gen.pch.T04PaymentH;
import cds.gen.pch.T05PaymentD;
import cds.gen.sys.T06DocNo;
import cds.gen.sys.T11IfManager;
import customer.bean.mst.Value;
import customer.bean.pch.SupList;
import customer.bean.pch.SapPchRoot;
import customer.bean.pch.SapPrRoot;
import customer.bean.pch.SapSupRoot;
import customer.bean.mst.SapMstRoot;
import customer.dao.mst.MaterialDataDao;
import customer.dao.sys.IFSManageDao;
import customer.dao.sys.SysD008Dao;
import customer.odata.S4OdataTools;

@Component
public class Ifm05PayService {

    @Autowired
    private IFSManageDao ifsManageDao;


    public String syncPr() {

        try {

        T11IfManager interfaceConfig = ifsManageDao.getByCode("IFM042");
 
           if (interfaceConfig != null) {

            String response = S4OdataTools.get(interfaceConfig, null, null, null);

                SapSupRoot sapsupRoot = JSON.parseObject(response, SapSupRoot.class);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
             
            for( SupList suplist : sapsupRoot.getSupList()){

                T04PaymentH o = T04PaymentH.create();

                o.setInvNo(suplist.getSupplierinvoice());
                o.setGlYear(suplist.getFiscalyear());
                o.setSupplier(suplist.getInvoicingparty());
                // o.setInvDate(suplist.getDocumentdate());
                o.setInvPostDate(suplist.getPostingdate());
                o.setExchange(suplist.getExchangerate());
                o.setInvBaseDate(suplist.getDuecalculationbasedate());

                T05PaymentD p = T05PaymentD.create();

                p.setInvNo(suplist.getSupplierinvoice());
                p.setGlYear(suplist.getFiscalyear());
                p.setItemNo(suplist.getSupplierinvoiceitem());
                p.setPoNo(suplist.getPurchaseorder());
                p.setDNo(suplist.getPurchaseorderitem());
                p.setTaxCode(suplist.getTaxcode());
                p.setTaxAmount(suplist.getTaxamount());
                p.setMatId(suplist.getPurchaseorderitemmaterial());
                p.setCurrency(suplist.getDocumentcurrency());
                p.setPriceAmount(suplist.getSupplierinvoiceitemamount());
                p.setQuantity(suplist.getQuantityinpurchaseorderunit());
                p.setUnit(suplist.getPurchaseorderquantityunit());
                p.setUnitPrice(suplist.getUnitprice());
                p.setTotalAmount(suplist.getTotalamount());
                p.setCostCenter(suplist.getCostcenter());
                p.setGlAccount(suplist.getGlaccount());
                p.setPoTrackNo(suplist.getRequirementtracking());
                p.setPrBy(suplist.getRequisitionername());
                p.setPurchaseGroup(suplist.getPurchasinggroup());
                p.setPurchaseGroupDesc(suplist.getPurchasinggroupname());
                p.setPlantId(suplist.getPlant());

            }

            return "success";
        } else {

            return "error";
        }

    } catch (Exception e) {

        return e.getMessage();

    }

}

}



