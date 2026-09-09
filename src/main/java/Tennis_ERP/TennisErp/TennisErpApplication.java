package Tennis_ERP.TennisErp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TennisErpApplication {

	public static void main(String[] args) {
		SpringApplication.run(TennisErpApplication.class, args);
	}

}
