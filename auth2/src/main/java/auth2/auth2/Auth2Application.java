package auth2.auth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Auth2Application {

	public static void main(String[] args) {

		SpringApplication.run(Auth2Application.class, args);
	}

}
