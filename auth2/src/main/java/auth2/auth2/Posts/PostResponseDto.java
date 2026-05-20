package auth2.auth2.Posts;

import java.time.LocalDateTime;

public class PostResponseDto {

  private  long postId;
  private String content;
  private String status;
  private LocalDateTime createdAt;

    public PostResponseDto(long postId, String content, String status, LocalDateTime createdAt) {
        this.postId = postId;
        this.content = content;
        this.status = status;
        this.createdAt = createdAt;
    }

    public PostResponseDto() {
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
