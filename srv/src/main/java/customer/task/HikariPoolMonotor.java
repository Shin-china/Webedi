package customer.task;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.sap.cds.ql.Select;
import com.sap.cds.services.persistence.PersistenceService;
import com.zaxxer.hikari.HikariPoolMXBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class HikariPoolMonotor {

    @Autowired
    private PersistenceService db;

    private static final Logger logger = LoggerFactory.getLogger(HikariPoolMonotor.class);

    private static long poolMonitorTimes = 0;

    @Scheduled(cron = "0 */10 * * * ?")
    public void poolMonitor() throws IOException {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName poolName;

        try {

            if (poolMonitorTimes == 0) { // 初次使用的时候主动连接下数据库
                logger.info("开始连接数据查询工厂");

                Thread.sleep(5000); // 等待5秒钟 让连接池初始化完成。
            }

            poolName = new ObjectName("com.zaxxer.hikari:type=Pool (usapHikariPool)");
            HikariPoolMXBean poolProxy = JMX.newMXBeanProxy(mBeanServer, poolName, HikariPoolMXBean.class);

            StringBuffer s = new StringBuffer();
            poolMonitorTimes++;
            s.append("(" + poolMonitorTimes + ")Pool Monitor: IdleConnections:" + poolProxy.getIdleConnections());
            s.append(" ActiveConnections:" + poolProxy.getActiveConnections());
            s.append(" TotalConnections:" + poolProxy.getTotalConnections());
            s.append(" ThreadsAwaitingConnections:" + poolProxy.getThreadsAwaitingConnection());
            logger.info(s.toString());

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}
