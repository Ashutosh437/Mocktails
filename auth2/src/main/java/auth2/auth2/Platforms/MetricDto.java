package auth2.auth2.Platforms;

public class MetricDto {
    private String platform;
    private long likes;
    private long comments;
    private long shares;
    private long impressions;

    public MetricDto() {}

    public MetricDto(String platform, long likes, long comments, long shares, long impressions) {
        this.platform = platform;
        this.likes = likes;
        this.comments = comments;
        this.shares = shares;
        this.impressions = impressions;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public long getShares() {
        return shares;
    }

    public void setShares(long shares) {
        this.shares = shares;
    }

    public long getImpressions() {
        return impressions;
    }

    public void setImpressions(long impressions) {
        this.impressions = impressions;
    }
}
