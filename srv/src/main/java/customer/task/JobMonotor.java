package customer.task;

import java.io.IOException;
import com.sap.cds.services.persistence.PersistenceService;

import customer.service.ifm.Ifm01BpService;

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

    @Scheduled(cron = "0 0 2 * * ?")
    public void poolMonitor() throws IOException {

        // IMFS01
        System.out.println("BP Sync run");
        ifm01BpService.syncBP();
        System.out.println("JobMonotor  run");

    }
}
