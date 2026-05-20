package auth2.auth2.Posts;

import java.time.LocalDateTime;
import java.util.List;

public class PostRequestDto {

    private String content;
    private boolean publish;
     private List<Platforms> platformsList;
     private LocalDateTime scheduledAt;
    public PostRequestDto(String content, boolean publish,List<Platforms>platformsList) {
        this.content = content;
        this.publish = publish;
        this.platformsList = platformsList;
    }

    public PostRequestDto() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public List<Platforms> getPlatformsList() {
        return platformsList;
    }

    public void setPlatformsList(List<Platforms> platformsList) {
        this.platformsList = platformsList;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }
}
