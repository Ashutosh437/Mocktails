package auth2.auth2.users;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Ping {

    @GetMapping("/ping")
        public String ping() {
        System.out.println("pringihohsdo;");
            return "App is running";
        }
    }

