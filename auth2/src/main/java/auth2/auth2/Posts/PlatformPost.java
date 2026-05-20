package auth2.auth2.Posts;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class PlatformPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Enumerated(EnumType.STRING)
    private Platforms platform;
    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;
    private LocalDateTime publishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="post_id",referencedColumnName ="id",nullable = false)
    private Post post;
    private String externalPostId;
    @Column(columnDefinition = "TEXT")
    private String errorMessage ;
    @Column(columnDefinition = "TEXT")
     private String metadata; // JSON

    public PlatformPost(long id, Platforms platform, PostStatus postStatus, LocalDateTime publishedAt, Post post, String externalPostId, String errorMessage,String metadata) {
        this.id = id;
        this.platform = platform;
        this.postStatus = postStatus;
        this.publishedAt = publishedAt;
        this.post = post;
        this.externalPostId = externalPostId;
        this.errorMessage = errorMessage;
        this.metadata = metadata;
    }

    public PlatformPost() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Platforms getPlatform() {
        return platform;
    }

    public void setPlatform(Platforms platform) {
        this.platform = platform;
    }

    public PostStatus getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getExternalPostId() {
        return externalPostId;
    }

    public void setExternalPostId(String externalPostId) {
        this.externalPostId = externalPostId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
