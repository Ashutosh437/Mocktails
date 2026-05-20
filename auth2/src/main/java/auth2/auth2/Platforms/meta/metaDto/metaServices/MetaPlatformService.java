package auth2.auth2.Platforms.meta.metaDto.metaServices;

import auth2.auth2.Platforms.MetricDto;
import auth2.auth2.Platforms.PlatformResultDto;
import auth2.auth2.Platforms.PlatformsServiceInterface;
import auth2.auth2.Posts.*;
import auth2.auth2.Posts.Platforms;
import auth2.auth2.accounts.ConnectedAccounts;
import auth2.auth2.accounts.ConnectedAccountsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MetaPlatformService implements PlatformsServiceInterface {

    @Autowired
    PlatformPostRepository platformpostrepository;
    @Autowired
    ConnectedAccountsRepo accountsRepo;
    @Autowired
    private RestClient restClient;

    @Override
    public Platforms getPlatform() {
        return Platforms.META;
    }

    @Override
    public PlatformResultDto Publish(Post post, int userId) {
        PlatformResultDto resultDto = new PlatformResultDto();
           if(post.getUserId()!=userId){
               throw new RuntimeException("user_id is not matched with user_id on the post");
           }

           List<ConnectedAccounts> accountsList = accountsRepo.findAll().stream()
                   .filter(accounts-> "meta".equals(accounts.getProvider()) && accounts.getUser().getId()==userId).toList();

            String message = post.getContent();

            for(ConnectedAccounts accounts : accountsList){
                String page_id = accounts.getSub();
                String post_Api = "https://graph.facebook.com/v19.0/"+page_id+"/feed";
                String page_token = accounts.getAccessToken();

                try{
                    Map<String,String> body = new HashMap<>();
                    body.put("message",message);

                   Map response= restClient.post()
                             .uri(post_Api)
                             .header("Authorization","Bearer "+page_token)
                             .body(body)
                             .retrieve()
                             .body(Map.class);
                      resultDto.setSuccess(true);
                      resultDto.setExternalPostId((String)response.get("id"));
                } catch (Exception e){
                    resultDto.setSuccess(false);
                    resultDto.setErrorMessage(e.getMessage());
                }

            }

        return resultDto;
    }

    @Override
    public MetricDto getMetrics(String externalPostId, int userId) {
        List<ConnectedAccounts> accountsList = accountsRepo.findAll().stream()
                .filter(accounts -> "meta".equals(accounts.getProvider()) && accounts.getUser().getId() == userId).toList();

        if (accountsList.isEmpty()) return new MetricDto("META", 0, 0, 0, 0);

        ConnectedAccounts account = accountsList.get(0);
        String accessToken = account.getAccessToken();
        String url = "https://graph.facebook.com/v19.0/" + externalPostId + "/insights?metric=post_impressions,post_engaged_users";

        try {
            Map response = restClient.get()
                    .uri(url)
                    .header("Authorization", "Bearer " + accessToken)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("data")) {
                List<Map> data = (List<Map>) response.get("data");
                long impressions = 0;
                long engagement = 0;

                for (Map metric : data) {
                    String name = (String) metric.get("name");
                    List<Map> values = (List<Map>) metric.get("values");
                    if (values != null && !values.isEmpty()) {
                        long val = ((Number) values.get(0).get("value")).longValue();
                        if ("post_impressions".equals(name)) impressions = val;
                        else if ("post_engaged_users".equals(name)) engagement = val;
                    }
                }
                return new MetricDto("META", 0, engagement, 0, impressions);
            }
        } catch (Exception e) {
            System.err.println("Error fetching Meta metrics: " + e.getMessage());
        }
        return new MetricDto("META", 0, 0, 0, 0);
    }
}