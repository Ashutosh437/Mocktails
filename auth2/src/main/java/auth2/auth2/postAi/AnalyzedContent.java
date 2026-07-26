package auth2.auth2.postAi;

import java.util.List;

public class AnalyzedContent {
    private String summary;
    private List<String> keyPoints;
    private List<String> suggestedHashtags;

    public AnalyzedContent() {}

    public AnalyzedContent(String summary, List<String> keyPoints, List<String> suggestedHashtags) {
        this.summary = summary;
        this.keyPoints = keyPoints;
        this.suggestedHashtags = suggestedHashtags;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getKeyPoints() {
        return keyPoints;
    }

    public void setKeyPoints(List<String> keyPoints) {
        this.keyPoints = keyPoints;
    }

    public List<String> getSuggestedHashtags() {
        return suggestedHashtags;
    }

    public void setSuggestedHashtags(List<String> suggestedHashtags) {
        this.suggestedHashtags = suggestedHashtags;
    }
}
