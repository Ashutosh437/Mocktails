package auth2.auth2.Platforms.meta.metaDto.metaControllers;

import auth2.auth2.Platforms.meta.metaDto.MetaPagesResponseDto;
import auth2.auth2.Platforms.meta.metaDto.MetaTokenResponseDto;
import auth2.auth2.States.oAuthState;
import auth2.auth2.States.oAuthStateRepo;
import auth2.auth2.users.Users;
import auth2.auth2.users.UsersRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.Nullable;
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
import auth2.auth2.accounts.*;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.UUID;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.CLIENT_SECRET;

@RestController
@RequestMapping("/oauth/meta")
public class MetaLoginController {
    @Autowired
    oAuthStateRepo oauthstaterepo;
    @Autowired
    UsersRepo usersRepo;
    @Autowired
    connectAccountsService accountsService;

    private String App_id ="2968006733590113";
    private String App_secret ="77a81248253d97ca77d3a6339703d67f";
    private final String REDIRECT_URI = "http://localhost:8080/oauth/meta/callback";

    @GetMapping("/login")
    public ResponseEntity<String> login(HttpServletResponse httpServletReponse) throws IOException {

       Authentication authentication  = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        int userId = usersRepo.findByUserEmail(email).get().getId();

        String state = UUID.randomUUID().toString();

        oAuthState oauthState = new oAuthState();
        oauthState.setState(state);
        oauthState.setId(userId); // store userId
        oauthstaterepo.save(oauthState);
        String url = "https://www.facebook.com/v19.0/dialog/oauth" +
                "?client_id="+App_id +
                "&redirect_uri=http://localhost:8080/oauth/meta/callback" +
                "&scope=pages_show_list,pages_read_engagement,pages_manage_posts"+
                "&response_type=code"+
                "&state="+state;

          return  ResponseEntity.ok(url);
    }


      @GetMapping("/callback")
    public void metaCallback(@RequestParam("code")String code,
                                   @RequestParam("state")String state,
                             HttpServletResponse httpServletResponse)throws IOException{
  
          oAuthState oauthState = oauthstaterepo.findById(state)
                  .orElseThrow(() -> new RuntimeException("Invalid state"));

          Users user = usersRepo.findById(oauthState.getId())
                  .orElseThrow(() -> new RuntimeException("User not found"));


          RestClient restClient = RestClient.create();

          //  2. Exchange code → USER ACCESS TOKEN
          String tokenUrl = "https://graph.facebook.com/v19.0/oauth/access_token";
          MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
          requestMap.add("grant_type", "authorization_code");
          requestMap.add("code", code);
          requestMap.add("redirect_uri", REDIRECT_URI);
          requestMap.add("client_id", App_id);
          requestMap.add("client_secret", App_secret);
          MetaTokenResponseDto tokenResponse = restClient.post()
                  .uri(tokenUrl)
                  .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                  .body(requestMap)
                  .retrieve()
                  .body(MetaTokenResponseDto.class);

          String access_token = tokenResponse.getAccess_token();
          if (tokenResponse == null || tokenResponse.getAccess_token() == null) {
              throw new RuntimeException("Failed to get access token from Meta");
          }

          // we have access token of user, now we need to get the access_token of pages users
          // facebook account

          String response= restClient.get()
                  .uri("https://graph.facebook.com/v19.0/me/accounts")
                  .header("Authorization","Bearer "+access_token)
                  .retrieve()
                  .body(String.class);

          ObjectMapper mapper  = new ObjectMapper();
       MetaPagesResponseDto metaPagesResponseDto = mapper.readValue(response, MetaPagesResponseDto.class);

          pagesTokenStoring(user,metaPagesResponseDto);
          oauthstaterepo.deleteById(state);
          System.out.println(response);
          System.out.println("meta connection is succesfull");


          httpServletResponse.sendRedirect("http://127.0.0.1:5500/login.html");
    }

     public void pagesTokenStoring(Users user, MetaPagesResponseDto metaPagesResponseDto){

           for(MetaPagesResponseDto.PageData pageData : metaPagesResponseDto.getData()){
               try {
                     String token = pageData.getAccess_token();
                     String provider = "meta";
                     String sub = pageData.getId();
                     String account_name = pageData.getName();
                   accountsService.createAccount(token,-1,provider,sub,user,account_name );

               }
               catch (Exception e){
                   throw new RuntimeException("account of the following page of user is not being created "+e);
               }
           }

          }


}
