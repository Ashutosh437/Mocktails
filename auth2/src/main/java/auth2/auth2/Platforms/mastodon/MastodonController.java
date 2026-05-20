package auth2.auth2.Platforms.mastodon;

import auth2.auth2.States.oAuthState;
import auth2.auth2.States.oAuthStateRepo;
import auth2.auth2.accounts.connectAccountsService;
import auth2.auth2.users.Users;
import auth2.auth2.users.UsersRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("oauth/mastodon")
public class MastodonController {
    @Autowired
    oAuthStateRepo oAuthStateRepo;
    @Autowired
    UsersRepo usersRepo;
    @Autowired
    connectAccountsService accountsService;

    String clientId = "9CLWuJqUOcOvfRksDaS9LaxUrqfrApwZCJlIsYPHjXg";
    String clientSecret = "1iytfoiwOsOjpdv8JGa0kvouWIM90W12xwkXTuPNVRs";
    String accessToken = "VvQSiJQ044hfk0yMW1ugifo38DjfCFZpHBajMUgv4sI";
    
    private final String REDIRECT_URI = "http://localhost:8080/oauth/mastodon/callback";
    private final String MASTODON_INSTANCE = "mastodon.social";

    @GetMapping("/login")
    public ResponseEntity<String> login(HttpServletResponse httpServletResponse) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        int userId = usersRepo.findByUserEmail(email).get().getId();

        String state = UUID.randomUUID().toString();

        oAuthState oauthState = new oAuthState();
        oauthState.setState(state);
        oauthState.setId(userId);
        oAuthStateRepo.save(oauthState);

        String url = "https://" + MASTODON_INSTANCE + "/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + REDIRECT_URI +
                "&response_type=code" +
                "&scope=read+write" +
                "&state=" + state;

        return ResponseEntity.ok(url);
    }

    @GetMapping("/callback")
    public void mastodonCallback(@RequestParam("code") String code,
                                 @RequestParam("state") String state,
                                 HttpServletResponse httpServletResponse) throws Exception {

        oAuthState oauthState = oAuthStateRepo.findById(state)
                .orElseThrow(() -> new RuntimeException("Invalid state"));

        Users user = usersRepo.findById(oauthState.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        RestClient restClient = RestClient.create();

        // Exchange code for access token
        String tokenUrl = "https://" + MASTODON_INSTANCE + "/oauth/token";
        MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
        requestMap.add("grant_type", "authorization_code");
        requestMap.add("code", code);
        requestMap.add("client_id", clientId);
        requestMap.add("client_secret", clientSecret);
        requestMap.add("redirect_uri", REDIRECT_URI);

        MastodonTokenResponseDto tokenResponse = restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(requestMap)
                .retrieve()
                .body(MastodonTokenResponseDto.class);

        if (tokenResponse == null || tokenResponse.getAccess_token() == null) {
            throw new RuntimeException("Failed to get access token from Mastodon");
        }

        String fetchedAccessToken = tokenResponse.getAccess_token();

        // Verify credentials to get username
        String verifyUrl = "https://" + MASTODON_INSTANCE + "/api/v1/accounts/verify_credentials";
        MastodonAccountDto accountDto = restClient.get()
                .uri(verifyUrl)
                .header("Authorization", "Bearer " + fetchedAccessToken)
                .retrieve()
                .body(MastodonAccountDto.class);

        String username = "Mastodon Account";
        if (accountDto != null && accountDto.getUsername() != null) {
            username = accountDto.getUsername();
        }

        // Save connected account
        accountsService.createAccount(fetchedAccessToken, -1, "MASTODON", MASTODON_INSTANCE, user, username);

        // Delete state
        oAuthStateRepo.deleteById(state);
        System.out.println("Mastodon connection is successful for user: " + username);

        // Redirect back to frontend
        httpServletResponse.sendRedirect("http://127.0.0.1:5500/posts.html");
    }
}
