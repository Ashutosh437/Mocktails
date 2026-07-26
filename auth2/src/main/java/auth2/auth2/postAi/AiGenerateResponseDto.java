package auth2.auth2.postAi;

import auth2.auth2.Posts.Platforms;
import java.util.Map;

public class AiGenerateResponseDto {

    private Map<Platforms, String> generatedDrafts;

    public AiGenerateResponseDto() {
    }

    public AiGenerateResponseDto(Map<Platforms, String> generatedDrafts) {
        this.generatedDrafts = generatedDrafts;
    }

    public Map<Platforms, String> getGeneratedDrafts() {
        return generatedDrafts;
    }

    public void setGeneratedDrafts(Map<Platforms, String> generatedDrafts) {
        this.generatedDrafts = generatedDrafts;
    }
}
