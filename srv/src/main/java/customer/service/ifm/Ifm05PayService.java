package customer.service.ifm;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cds.gen.pch.T04PaymentH;
import cds.gen.pch.T05PaymentD;
import cds.gen.sys.T06DocNo;
import cds.gen.sys.T11IfManager;
import customer.bean.mst.Value;
import customer.bean.pch.SupList;
import customer.comm.tool.MessageTools;
import customer.bean.pch.SapPchRoot;
import customer.bean.pch.SapPrRoot;
import customer.bean.pch.SapSupRoot;
import customer.bean.ifm.IFLog;
import customer.bean.mst.SapMstRoot;
import customer.dao.mst.MaterialDataDao;
import customer.dao.pch.PurchaseDataDao;
import customer.dao.sys.IFSManageDao;
import customer.dao.sys.SysD008Dao;
import customer.odata.S4OdataTools;
import customer.service.comm.IfmService;

@Component
public class Ifm05PayService extends IfmService {

    @Autowired
    private IFSManageDao ifsManageDao;

    @Autowired
    private PurchaseDataDao PchDao;

    private SapSupRoot get(IFLog log) throws Exception {

        T11IfManager info = this.getIfMnager(log);

        // 调用 Web Service 的 get 方法
        String a = S4OdataTools.get(info, 1000, null, null);
        return JSON.parseObject(a, SapSupRoot.class);

    }

    public void process(IFLog log) {

        log.setTd(super.transactionInit()); // 事务初始换

        try {
            this.insertLog(log);// 插入日志

            SapSupRoot data = get(log);

            // log.setTotalNum(data.get__count());// 得到记录总数
            // int pageCount = log.getPageCount(); // 得到页数

            onePage(log, data.getItems()); // 处理第0页的数据

            log.setSuccessMsg(MessageTools.getMsgText(rbms, "IFM05_01", log.getSuccessNum(), log.getErrorNum(),
                    log.getConsumTimeS()));

        } catch (Exception e) {
            e.printStackTrace();
            log.setFairMsg(e.getMessage());
        } finally {
            log.setFinish(); // 设定接口完了时间
            this.updateLog(log);

        }

    }

    private void onePage(IFLog log, ArrayList<SupList> items) {

        if (items != null && items.size() > 0) {

            for (SupList suplist : items) {

                TransactionStatus s = null;

                try {

                    s = this.begin(log.getTd()); // 开启新事务

                    T04PaymentH o = T04PaymentH.create();

                    o.setInvNo(suplist.getSupplierinvoice());
                    o.setGlYear(suplist.getFiscalyear());
                    o.setSupplier(suplist.getInvoicingparty());
                    o.setInvPostDate(suplist.getPostingdate2());
                    o.setExchange(suplist.getExchangerate());
                    o.setInvBaseDate(suplist.getDuecalculationbasedate());
                    o.setAmount(suplist.getInvoicegrossamount());
                    o.setTaxAmount(suplist.getTaxamountheader());
                    o.setHeaderText(suplist.getDocumentheadertext());
                    o.setSendFlag(suplist.getSendflag());
                    o.setSupplierDescription(suplist.getSuppliername());
                    o.setInvDate(suplist.getDocumentdate());

                    PchDao.modifyT04(o);

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
                    p.setCompanyCode(suplist.getCompanycode());
                    p.setShkzg(suplist.getDebitcreditcode());
                    p.setGrDate(suplist.getPostingdate1());
                    p.setTaxRate(suplist.getTaxrate());
                    p.setMatDesc(suplist.getPurchaseorderitemtext());

                    PchDao.modifyT05(p);
                    log.addSuccessNum();
                    this.commit(s);

                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    this.rollback(s); // 回滚事务
                }

            }
        }
    }


}
