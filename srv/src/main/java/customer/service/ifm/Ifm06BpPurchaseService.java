package customer.service.ifm;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;

import com.alibaba.fastjson.JSON;

import cds.gen.mst.T05SapBpPurchase;
import cds.gen.sys.T11IfManager;
import customer.bean.bp.D;
import customer.bean.bp.Results;
import customer.bean.bp.SapBpRoot;

import customer.bean.ifm.IFLog;
import customer.comm.tool.MessageTools;
import customer.dao.mst.BPPurchaseDao;
import customer.odata.S4OdataTools;
import customer.service.comm.IfmService;

@Repository
public class Ifm06BpPurchaseService extends IfmService {

    private static final Logger logger = LoggerFactory.getLogger(Ifm06BpPurchaseService.class);

    @Autowired
    private BPPurchaseDao bpPurchaseDao;

    private D getBpPurchase(IFLog log, Integer currentPage) throws Exception {
        T11IfManager info = this.getIfMnager(log);

        HashMap<String, String> addParaMap = S4OdataTools.setCountPara(false, null);

        String a = S4OdataTools.get(info, currentPage, addParaMap, null);
        SapBpRoot ps = JSON.parseObject(a, SapBpRoot.class);
        return ps.getD();
    }

    public void process(IFLog log) {

        log.setTd(super.transactionInit()); // 事务初始换

        try {
            this.insertLog(log);// 插入日志
            D data = getBpPurchase(log, 0);

            log.setTotalNum(data.get__count());// 得到记录总数
            int pageCount = log.getPageCount(); // 得到页数
            onePage(log, data.getResults()); // 处理第0页的数据

            if (pageCount > 1) {
                for (int i = 1; i < pageCount; i++) {
                    data = getBpPurchase(log, i);
                    onePage(log, data.getResults());
                }

            }

            log.setSuccessMsg(MessageTools.getMsgText(rbms, "IFM04_01", log.getSuccessNum(), log.getErrorNum(),
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

                T05SapBpPurchase o = T05SapBpPurchase.create();
                o.setSupplier(v.getSupplier());
                o.setPurchaseOrg(v.getPurchasingOrganization());
                o.setZabc(v.getSupplierABCClassificationCode());

                bpPurchaseDao.modify(o);

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
