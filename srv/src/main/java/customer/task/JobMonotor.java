package customer.task;

import java.io.IOException;
import com.sap.cds.services.persistence.PersistenceService;

import customer.bean.ifm.IFLog;
import customer.dao.sys.IFSManageDao;
import customer.service.ifm.Ifm01BpService;
import customer.service.ifm.Ifm02MstService;
// import customer.service.ifm.Ifm03PoService;
import customer.service.ifm.Ifm03PoService;
import customer.service.ifm.Ifm04PrService;
// import customer.service.ifm.Ifm05PrService;
import customer.service.ifm.Ifm05PayService;
import customer.service.ifm.Ifm06BpPurchaseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class JobMonotor {

    private static final Logger logger = LoggerFactory.getLogger(HikariPoolMonotor.class);

    private static long poolMonitorTimes = 0;

    @Autowired
    private Ifm01BpService ifm01BpService;

    @Autowired
    private Ifm02MstService ifm02MstService;

    @Autowired
    private Ifm03PoService ifm03PoService;

    @Autowired
    private Ifm04PrService ifm04PrService;

    @Autowired
    private Ifm05PayService ifm05PayService;

    @Autowired
    private Ifm06BpPurchaseService ifm06BpPurchaseService;

    // 改造 定时任务
    @Scheduled(cron = "0 1 13 * * *") // 中国时间21:01点执行
    public void poolMonitor() throws IOException {
        // BP 同期 标准api
        IFLog ifLog = new IFLog(IFSManageDao.IF_S4_BP);
        ifm01BpService.process(ifLog);
    }

    @Scheduled(cron = "0 2 13 * * *") // 中国时间21:02点执行
    public void poolMonitor6() throws IOException {
        // BP PURCHASE 标准api
        IFLog ifLog = new IFLog(IFSManageDao.IF_S4_BPPURCHASE);
        ifm06BpPurchaseService.process(ifLog);
    }

    @Scheduled(cron = "0 0/15 * * * ?") // 每15分钟执行一次
    public void poolMonitor1() throws IOException {
        // IF039 品目同期 标准api
        IFLog ifLog = new IFLog(IFSManageDao.IF_S4_MST);
        ifm02MstService.process(ifLog, null);

    }

    @Scheduled(cron = "0 0/15 * * * ?")
    public void poolMonitor3() throws IOException {

        // IF041 po同期 自开发
        IFLog ifLog = new IFLog(IFSManageDao.IF_S4_PO);
        ifm03PoService.process(ifLog);

    }

    @Scheduled(cron = "0 0/15 * * * ?")
    public void poolMonitor4() throws IOException {

        // IF065 フォーキャスト連携 自开发
        IFLog ifLog = new IFLog(IFSManageDao.IF_S4_PR);
        ifm04PrService.process(ifLog);

    }

    @Scheduled(cron = "0 0/15 * * * ?")
    public void poolMonitor5() throws IOException {

        // IF042 PO支払通知同期 自开发
        IFLog ifLog = new IFLog(IFSManageDao.IF_S4_PAY);
        ifm05PayService.process(ifLog);

    }

}