package auth2.auth2.Platforms.linkedln.linkedinServices;

import auth2.auth2.Platforms.MetricDto;
import auth2.auth2.Platforms.PlatformResultDto;
import auth2.auth2.Platforms.PlatformsServiceInterface;
import auth2.auth2.Posts.Platforms;
import auth2.auth2.Posts.Post;
import auth2.auth2.accounts.ConnectedAccounts;
import auth2.auth2.accounts.ConnectedAccountsRepo;
import auth2.auth2.users.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class linkedinPlatformService implements PlatformsServiceInterface {
    @Autowired
    UsersRepo usersRepo;
    @Autowired
    ConnectedAccountsRepo accountsRepo;
    @Autowired
    private RestClient restClient;


    @Override
    public Platforms getPlatform() {
        return Platforms.LINKEDIN;
    }

    @Override
    public PlatformResultDto Publish(Post post, int userId) {
        System.out.println("publish method is being called");
        System.out.println("Publish User ID: " +  userId);

        List<ConnectedAccounts> accountsList = accountsRepo.findAll().stream()
                .filter(accounts -> "linkedin".equals(accounts.getProvider()) && accounts.getUser().getId() == userId)
                .toList();

        PlatformResultDto platformResultDto = new PlatformResultDto();
        if (accountsList.isEmpty()) {
            platformResultDto.setSuccess(false);
            platformResultDto.setErrorMessage("linkedin not connected");
            return platformResultDto;
        }

        ConnectedAccounts connectedaccount = accountsList.get(0);
        String accesstoken = connectedaccount.getAccessToken();
        String sub = connectedaccount.getSub();
        String urn = "urn:li:person:"+sub;
        String Url = "https://api.linkedin.com/v2/ugcPosts";


          try {
              Map<String, Object> map = new HashMap<>();
              map.put("author", urn);
              map.put("lifecycleState", "PUBLISHED");

              map.put("specificContent",
                      Map.of("com.linkedin.ugc.ShareContent",
                              Map.of(
                                      "shareCommentary",
                                      Map.of("text", post.getContent()),
                                      "shareMediaCategory", "NONE"
                              )));

              map.put("visibility",
                      Map.of("com.linkedin.ugc.MemberNetworkVisibility", "PUBLIC"));

              ObjectMapper objectMapper = new ObjectMapper();
              String requests = objectMapper.writeValueAsString(map);
              var response = restClient.post()
                      .uri(Url)
                      .header(HttpHeaders.AUTHORIZATION, "Bearer " + accesstoken)
                      .header("X-Restli-Protocol-Version", "2.0.0")
                      .header(HttpHeaders.ACCEPT,"application/json")
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(requests)
                      .retrieve()
                      .toEntity(String.class);
              if (response.getStatusCode().is2xxSuccessful()) {
                  String externalId = response.getHeaders().getFirst("x-restli-id");
                  platformResultDto.setSuccess(true);
                  platformResultDto.setExternalPostId(externalId);
              } else {
                  String errorBody = new String(response.getBody().getBytes(StandardCharsets.UTF_8));
                  platformResultDto.setSuccess(false);
                  platformResultDto.setErrorMessage("LINKEDIN ERROR" + errorBody);

              }
          } catch (Exception e){
               platformResultDto.setSuccess(false);
               platformResultDto.setErrorMessage(e.getMessage());
          }

      return platformResultDto;
    }

    @Override
    public MetricDto getMetrics(String externalPostId, int userId) {
        List<ConnectedAccounts> accountsList = accountsRepo.findAll().stream()
                .filter(accounts -> "linkedin".equals(accounts.getProvider()) && accounts.getUser().getId() == userId)
                .toList();
        if (accountsList.isEmpty()) return new MetricDto("LINKEDIN", 0, 0, 0, 0);

        String accessToken = accountsList.get(0).getAccessToken();
        // Ensure externalPostId is a full URN if it's just an ID
        String urn = externalPostId.startsWith("urn:li:") ? externalPostId : "urn:li:share:" + externalPostId;
        String url = "https://api.linkedin.com/v2/socialActions/" + urn;

        try {
            Map response = restClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header("X-Restli-Protocol-Version", "2.0.0")
                    .retrieve()
                    .body(Map.class);

            if (response != null) {
                Map likesSummary = (Map) response.get("likesSummary");
                Map commentsSummary = (Map) response.get("commentsSummary");

                long likes = likesSummary != null ? ((Number) likesSummary.get("totalLikes")).longValue() : 0;
                long comments = commentsSummary != null ? ((Number) commentsSummary.get("totalComments")).longValue() : 0;

                return new MetricDto("LINKEDIN", likes, comments, 0, 0);
            }
        } catch (Exception e) {
            System.err.println("Error fetching LinkedIn metrics: " + e.getMessage());
        }
        return new MetricDto("LINKEDIN", 0, 0, 0, 0);
    }
}