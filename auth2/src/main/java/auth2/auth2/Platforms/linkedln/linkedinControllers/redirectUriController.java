package auth2.auth2.Platforms.linkedln.linkedinControllers;

import auth2.auth2.States.oAuthState;
import auth2.auth2.States.oAuthStateRepo;
import auth2.auth2.accounts.connectAccountsService;
import auth2.auth2.Platforms.linkedln.linkedinDto.LinkedinTokenResponseDto;
import auth2.auth2.Platforms.linkedln.linkedinDto.LinkedinUserInfoDto;
import auth2.auth2.users.Users;
import auth2.auth2.users.UsersRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@RestController
@RequestMapping("oauth2/callback")
public class redirectUriController {
    @Autowired
      private UsersRepo  usersRepo;
    @Autowired
    private oAuthStateRepo oauthstaterepo;
    @Autowired
    private connectAccountsService acccountsService;

    private static final Logger log = LoggerFactory.getLogger(redirectUriController.class);
    public String client_id ="86i7dioow4moay";
    public String client_secret ="WPL_AP1.XRneugsGQOTiOwZv.kJ6fXw==";
    public String redirect_uri ="http://localhost:8080/oauth2/callback/linkedin";




 @GetMapping("/linkedin")
    public void redirectUri
              (@RequestParam("code") String code, @RequestParam("state")String state, HttpServletResponse httpServletResponse) throws IOException {

     System.out.println("api is being hit successfully by linkedin");
     System.out.println("code is : "+code);

      // Match the state
     oAuthState oauthstate = oauthstaterepo.findById(state).
                                               orElseThrow(()-> new RuntimeException("states different"));


     String tokenUrl = "https://www.linkedin.com/oauth/v2/accessToken";
     MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
     requestMap.add("grant_type", "authorization_code");
     requestMap.add("code", code);
     requestMap.add("redirect_uri", redirect_uri);
     requestMap.add("client_id", client_id);
     requestMap.add("client_secret", client_secret);


     RestClient restClient = RestClient.create();
           LinkedinTokenResponseDto linkedinTokenResponseDto = restClient.post()
                       .uri(tokenUrl)
                       .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(requestMap)
                        .retrieve()
                         .body(LinkedinTokenResponseDto.class);
     if (linkedinTokenResponseDto == null) {
         throw new RuntimeException("Token exchange failed");
     }

     System.out.println( "the token is" + linkedinTokenResponseDto.getAccess_token());

     // storing the info in ConnectedAccounts

     String accessToken = linkedinTokenResponseDto.getAccess_token();
     int expiresIn = linkedinTokenResponseDto.getExpires_in();
     String sub = getsub(accessToken);
     // oauthstate.getId() returns the id of user not the state id,
     Users users = usersRepo.findById( oauthstate.getId()).get();

     System.out.println("Creating the account table now");
      try {
          acccountsService.createAccount(accessToken, expiresIn, "linkedin", sub, users);
          oauthstaterepo.deleteById(oauthstate.getState());
      } catch(Exception e){
           throw new RuntimeException("the account is not being created" + e);
      }

      httpServletResponse.sendRedirect("http://127.0.0.1:5500/login.html");
 }


   public String  getsub(String token){
        String userInfoUrl = "https://api.linkedin.com/v2/userinfo";
      RestClient restClient  = RestClient.create();
      LinkedinUserInfoDto linkedinUserInfoDto =
              restClient.get()
                      .uri(userInfoUrl)
                      .header("Authorization","Bearer "+token)
                      .retrieve()
                      .body(LinkedinUserInfoDto.class);


      return  linkedinUserInfoDto.getSub();
     
   }






}
