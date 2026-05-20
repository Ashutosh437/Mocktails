package auth2.auth2.Platforms;

import auth2.auth2.Posts.Platforms;
import auth2.auth2.Posts.Post;

public interface PlatformsServiceInterface {

    Platforms getPlatform();
    PlatformResultDto Publish(Post post,int userId);
    MetricDto getMetrics(String externalPostId, int userId);

}
