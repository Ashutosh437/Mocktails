package auth2.auth2.postAi;

import auth2.auth2.Posts.Platforms;
import java.util.List;

public class AiGenerateRequestDto {

    private String rawContent;
    private List<Platforms> targetPlatforms;
    private String tone;
    private String previousDraft;
    private String userFeedback;

    public AiGenerateRequestDto() {
    }

    public AiGenerateRequestDto(String rawContent, List<Platforms> targetPlatforms, String tone, String previousDraft, String userFeedback) {
        this.rawContent = rawContent;
        this.targetPlatforms = targetPlatforms;
        this.tone = tone;
        this.previousDraft = previousDraft;
        this.userFeedback = userFeedback;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public List<Platforms> getTargetPlatforms() {
        return targetPlatforms;
    }

    public void setTargetPlatforms(List<Platforms> targetPlatforms) {
        this.targetPlatforms = targetPlatforms;
    }

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
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
}
