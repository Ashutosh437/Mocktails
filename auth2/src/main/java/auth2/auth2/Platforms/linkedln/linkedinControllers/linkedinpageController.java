package auth2.auth2.Platforms.linkedln.linkedinControllers;

import auth2.auth2.States.oAuthState;
import auth2.auth2.States.oAuthStateRepo;
import auth2.auth2.users.UsersRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/oauth2/linkedin")
public class linkedinpageController {
    @Autowired
    oAuthStateRepo oauthstaterepo;
    @Autowired
    UsersRepo usersRepo;
    String clientid = "86i7dioow4moay";
    String redirect_uri ="http://localhost:8080/oauth2/callback/linkedin";



    @PostMapping("/login")
    public  ResponseEntity<String> linkedinScreen(HttpServletResponse httpServletResponse) throws IOException {

    String state = UUID.randomUUID().toString();

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       String email= authentication.getName();
       int userId = usersRepo.findByUserEmail(email).get().getId();

        oAuthState oauthstate = new oAuthState();
        oauthstate.setId(userId);
        oauthstate.setState(state);
        oauthstate.setCreatedAt(Instant.now());
        oauthstaterepo.save(oauthstate);

        String linkedinAuthUrl =
                "https://www.linkedin.com/oauth/v2/authorization" +
                        "?response_type=code" +
                        "&client_id=" + clientid +
                        "&redirect_uri=" + URLEncoder.encode(redirect_uri, StandardCharsets.UTF_8) +
                        "&scope=" + URLEncoder.encode(
                        "openid profile email w_member_social",
                        StandardCharsets.UTF_8) +
                        "&state=" + state;

             return ResponseEntity.ok(linkedinAuthUrl);
    }

}
