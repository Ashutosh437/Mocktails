package auth2.auth2.Platforms.mastodon;

import auth2.auth2.Platforms.MetricDto;
import auth2.auth2.Platforms.PlatformResultDto;
import auth2.auth2.Platforms.PlatformsServiceInterface;
import auth2.auth2.Posts.Platforms;
import auth2.auth2.Posts.Post;
import auth2.auth2.accounts.ConnectedAccounts;
import auth2.auth2.accounts.ConnectedAccountsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MastodonPlatformService implements PlatformsServiceInterface {

    @Autowired
    private ConnectedAccountsRepo accountsRepo;

    @Autowired
    private RestClient restClient;

    @Override
    public Platforms getPlatform() {
        return Platforms.MASTODON;
    }

    @Override
    public PlatformResultDto Publish(Post post, int userId) {
        List<ConnectedAccounts> accountsList = accountsRepo.findAll().stream()
                .filter(accounts -> "MASTODON".equals(accounts.getProvider()) && accounts.getUser().getId() == userId)
                .toList();
                
        if (accountsList.isEmpty()) {
            return new PlatformResultDto(false, "Mastodon not connected", null);
        }

        ConnectedAccounts account = accountsList.get(0);
        String accessToken = account.getAccessToken();
        // Use mastodon.social as default if not specified in sub
        String instance = account.getSub().contains(".") ? account.getSub() : "mastodon.social";
        String url = "https://" + instance + "/api/v1/statuses";

        try {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("status", post.getContent());

            Map response = restClient.post()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            if (response != null && response.containsKey("id")) {
                return new PlatformResultDto(true, null, (String) response.get("id"));
            }
        } catch (Exception e) {
            return new PlatformResultDto(false, "Mastodon error: " + e.getMessage(), null);
        }
        return new PlatformResultDto(false, "Failed to publish to Mastodon", null);
    }

    @Override
    public MetricDto getMetrics(String externalPostId, int userId) {
        List<ConnectedAccounts> accountsList = accountsRepo.findAll().stream()
                .filter(accounts -> "MASTODON".equals(accounts.getProvider()) && accounts.getUser().getId() == userId)
                .toList();
                
        if (accountsList.isEmpty()) return new MetricDto("MASTODON", 0, 0, 0, 0);

        ConnectedAccounts account = accountsList.get(0);
        String accessToken = account.getAccessToken();
        String instance = account.getSub().contains(".") ? account.getSub() : "mastodon.social";
        String url = "https://" + instance + "/api/v1/statuses/" + externalPostId;

        try {
            Map response = restClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(Map.class);

            if (response != null) {
                long likes = ((Number) response.getOrDefault("favourites_count", 0)).longValue();
                long shares = ((Number) response.getOrDefault("reblogs_count", 0)).longValue();
                long comments = ((Number) response.getOrDefault("replies_count", 0)).longValue();

                return new MetricDto("MASTODON", likes, comments, shares, 0);
            }
        } catch (Exception e) {
            System.err.println("Error fetching Mastodon metrics: " + e.getMessage());
        }
        return new MetricDto("MASTODON", 0, 0, 0, 0);
    }
}
