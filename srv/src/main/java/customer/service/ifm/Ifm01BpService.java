package customer.service.ifm;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.alibaba.fastjson.JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cds.gen.mst.T03SapBp;
import cds.gen.sys.T11IfManager;
import customer.bean.bp.D;
import customer.bean.bp.Results;
import customer.bean.bp.SapBpRoot;
import customer.bean.bp.To_AddressIndependentFax;

import customer.bean.bp.To_BusinessPartnerTax;
import customer.bean.ifm.IFLog;

import customer.comm.tool.MessageTools;

import customer.dao.mst.BusinessPartnerDao;

import customer.odata.S4OdataTools;
import customer.service.comm.IfmService;

@Component
public class Ifm01BpService extends IfmService {

    private static final Logger logger = LoggerFactory.getLogger(Ifm01BpService.class);

    // @Autowired
    // private IFSManageDao ifsManageDao;

    @Autowired
    private BusinessPartnerDao BPDao;

    private D getBp1(IFLog log, Integer currentPage) throws Exception {
        T11IfManager info = this.getIfMnager(log);

        HashMap<String, String> addParaMap = S4OdataTools.setCountPara(false, null);

        String a = S4OdataTools.get(info, currentPage, addParaMap, null);
        logger.info("bp1:{}", a);
        SapBpRoot ps = JSON.parseObject(a, SapBpRoot.class);
        logger.info("bp11:{}", ps);
        return ps.getD();
    }

    public void process(IFLog log) {

        log.setTd(super.transactionInit()); // 事务初始换

        try {
            this.insertLog(log);// 插入日志
            D data = getBp1(log, 0);
            log.setTotalNum(data.get__count());// 得到记录总数
            int pageCount = log.getPageCount(); // 得到页数
            onePage(log, data.getResults()); // 处理第0页的数据

            if (pageCount > 1) {
                for (int i = 1; i < pageCount; i++) {
                    data = getBp1(log, i);
                    onePage(log, data.getResults());
                }

            }

            log.setSuccessMsg(MessageTools.getMsgText(rbms, "IFM01_01", log.getSuccessNum(), log.getErrorNum(),
                    log.getConsumTimeS()));

        } catch (Exception e) {
            e.printStackTrace();
            log.setFairMsg(e.getMessage());
        } finally {
            log.setFinish(); // 设定接口完了时间
            this.updateLog(log);
        }

    }

    public IFLog onePage(IFLog log, List<Results> list) {

        String bpId = null;

        for (Results v : list) { // 单个Bp循环
            TransactionStatus s = null;
            try {

                bpId = v.getBusinessPartner();

                s = this.begin(log.getTd()); // 开启新事务

                T03SapBp o = T03SapBp.create(bpId);

                o.setBpType("SUPP");

                o.setBpName1(v.getOrganizationBPName1());
                o.setBpName2(v.getOrganizationBPName2());
                o.setBpName3(v.getOrganizationBPName3());
                o.setBpName4(v.getOrganizationBPName4());
                o.setSearch2(v.getSearchTerm2());

                for (To_AddressIndependentFax fax : v.getTo_AddressIndependentFax().getResults()) {
                    o.setFax(fax.getInternationalFaxNumber());
                }

                // Tax Number
                for (To_BusinessPartnerTax tax : v.getTo_BusinessPartnerTax().getResults()) {
                    o.setLogNo(tax.getBPTaxNumber());
                }

                o.setDelFlag("N");

                BPDao.modify(o);
                log.addSuccessNum();
                this.commit(s);
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("bp:{0}处理异常{1}", bpId, e.getMessage());
            } finally {
                this.rollback(s); // 回滚事务
            }
        }

        return log;
    }
}
