package auth2.auth2.Posts;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlatformPostRepository extends JpaRepository<PlatformPost, Long> {
    public List<PlatformPost> findByPost_id(long postId);
}
