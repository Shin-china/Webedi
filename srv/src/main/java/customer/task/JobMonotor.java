package customer.task;

import java.io.IOException;
import com.sap.cds.services.persistence.PersistenceService;

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

    @Scheduled(cron = "0 0/59 * * * ?")
    public void poolMonitor() throws IOException {

        // IMFS01 bp同期
        System.out.println("BP Sync run");
        ifm01BpService.syncBP();
        System.out.println("JobMonotor  run");

    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void poolMonitor6() throws IOException {

        // IMFS02 bp同期
        ifm06BpPurchaseService.syncBPPurchase();
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void poolMonitor1() throws IOException {

        // IF039 品目同期 标准api
        System.out.println("MST Sync run");
        ifm02MstService.syncMst();
        System.out.println("JobMonotor  run");
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void poolMonitor3() throws IOException {

        // IF041 po同期 自开发
        System.out.println("PO Sync run");
        ifm03PoService.syncPo();
        System.out.println("JobMonotor  run");
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void poolMonitor4() throws IOException {

        // IF065 フォーキャスト連携
        System.out.println("PR Sync run");
        ifm04PrService.syncPr();
        System.out.println("JobMonotor  run");
    }

    @Scheduled(cron = "0 0/5 * * * ?")
    public void poolMonitor5() throws IOException {

        // IF042 PO支払通知同期
        System.out.println("pr Sync run");
        ifm05PayService.syncPay();
        System.out.println("JobMonotor run");
    }

}