package customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

import java.net.URL;

@SpringBootApplication
@ComponentScan({ "com.sap.cloud.sdk", "customer" })
@ServletComponentScan({ "com.sap.cloud.sdk", "customer" })
@EnableAutoConfiguration(exclude = { SecurityAutoConfiguration.class })
public class Application {

	public static void main(String[] args) {
		URL url = Application.class.getResource("datasource.properties");
		if (url != null) {
			String path = url.getPath();
			System.setProperty("hikaricp.configurationFile", path);
		}
		SpringApplication.run(Application.class, args);
	}

}
