package auth2.auth2.postAi;

import auth2.auth2.Posts.Platforms;

public class AiRefineRequestDto {
    private Platforms platform;
    private String previousDraft;
    private String userFeedback;
    private String rawContent;

    public AiRefineRequestDto() {
    }

    public AiRefineRequestDto(Platforms platform, String previousDraft, String userFeedback, String rawContent) {
        this.platform = platform;
        this.previousDraft = previousDraft;
        this.userFeedback = userFeedback;
        this.rawContent = rawContent;
    }

    public Platforms getPlatform() {
        return platform;
    }

    public void setPlatform(Platforms platform) {
        this.platform = platform;
    }

    public String getPreviousDraft() {
        return previousDraft;
    }

    public void setPreviousDraft(String previousDraft) {
        this.previousDraft = previousDraft;
    }

    public String getUserFeedback() {
        return userFeedback;
    }

    public void setUserFeedback(String userFeedback) {
        this.userFeedback = userFeedback;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }
}
