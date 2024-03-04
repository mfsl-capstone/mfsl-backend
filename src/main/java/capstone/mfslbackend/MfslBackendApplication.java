package capstone.mfslbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class MfslBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MfslBackendApplication.class, args);
	}

}
