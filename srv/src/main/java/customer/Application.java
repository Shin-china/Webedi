package customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import customer.comm.constant.ConfigConstants;
import customer.dao.sys.T12ConfigDao;
import customer.odata.S4OdataTools;

import java.net.URL;
import java.time.ZoneOffset;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@ComponentScan({ "com.sap.cloud.sdk", "customer" })
@ServletComponentScan({ "com.sap.cloud.sdk", "customer" })
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling
public class Application implements CommandLineRunner {

	@Autowired
	T12ConfigDao configDao;

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		URL url = Application.class.getResource("datasource.properties");

		if (url != null) {
			String path = url.getPath();
			System.setProperty("hikaricp.configurationFile", path);
		}
		SpringApplication.run(Application.class, args);
	}

	public void run(String... args) throws Exception {

		// 设定服务器时区&主要用户时区&APP ID&默认语言
		ConfigConstants.SERVER_ZONE = ZoneOffset
		        .ofHours(Integer.parseInt(configDao.getConfig("SERVER_ZONE").getConValue()));
		ConfigConstants.S4_ODATA_ZONE_OFFSET = ZoneOffset
		        .ofHours(Integer.parseInt(configDao.getConfig("S4_ODATA_ZONE").getConValue()));
		ConfigConstants.DEFFAULT_USER_ZONE = ZoneOffset
		        .ofHours(Integer.parseInt(configDao.getConfig("USER_ZONE").getConValue()));
		ConfigConstants.BTP_APP_ID = configDao.getConfig("SYSTEM_ID").getConValue();
		ConfigConstants.USER_LANG_CODE = configDao.getConfig("USER_LANG_CODE").getConValue();

		// // 当前系统有几家工厂
		// ConfigConstants.SYSTEM_PLANT_LIST = Arrays
		// .asList(configDao.getConfig("SYSTEM_PLANT_ID").getConValue().split(","));

		// 设定S4 DESTINATION
		S4OdataTools.desName = configDao.getConfig("S4_DESTINATION").getConValue();
		log.info("desName: " + S4OdataTools.desName);

		log.info("init finish ************************************");
	}

}
