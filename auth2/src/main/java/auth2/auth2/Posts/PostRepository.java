package auth2.auth2.Posts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByUserId(long userId);
    List<Post> findByPostStatusAndScheduledAtBefore(PostStatus postStatus, LocalDateTime now);
}
