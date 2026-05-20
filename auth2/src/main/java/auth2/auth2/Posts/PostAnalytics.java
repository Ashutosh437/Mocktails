package auth2.auth2.Posts;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import org.springframework.web.bind.annotation.GetMapping;

public class PostAnalytics {

    private long id;

    private long likes;
    private long comments;
    private long shares;

   private long platformId;


}
