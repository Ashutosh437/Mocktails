package auth2.auth2.postAi;

import auth2.auth2.Posts.Platforms;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import auth2.auth2.postAi.AiGenerateRequestDto;
import auth2.auth2.postAi.AiGenerateResponseDto;

@Service
public class AiRepurposingService {

    private final ChatClient chatClient;

    public AiRepurposingService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * This private helper method directly interacts with the LLM (Gemini)
     * to generate or refine content for a specific platform.
     */
    private String generateOrRefineDraft(String rawContent, Platforms platform, String tone, String previousDraft,
            String userFeedback) {
        StringBuilder promptBuilder = new StringBuilder();

        if (StringUtils.hasText(previousDraft) && StringUtils.hasText(userFeedback)) {
            // Iterative Prompting Mode (Refining an existing draft)
            promptBuilder.append("You are an expert social media manager. Modify the following draft for ")
                    .append(platform).append(".\n");
            promptBuilder.append("User Feedback / Instructions: ").append(userFeedback).append("\n");
            promptBuilder.append("Original Source Context: ").append(rawContent).append("\n");
            promptBuilder.append("Previous Draft to modify: \n").append(previousDraft).append("\n");
        } else {
            // First-time Generation Mode
            promptBuilder.append(
                    "You are an expert social media manager. Convert the following content into a high-quality post specifically optimized for ")
                    .append(platform).append(".\n");
            if (StringUtils.hasText(tone)) {
                promptBuilder.append("The tone should be: ").append(tone).append(".\n");
            }
            promptBuilder.append("Raw Content: \n").append(rawContent).append("\n");
        }

        promptBuilder.append("\nReturn ONLY the exact text for the post, with no conversational filler.");

        // Call Spring AI Gemini
        return chatClient.prompt()
                .user(promptBuilder.toString())
                .call()
                .content();
    }

    public AiGenerateResponseDto generateDrafts(AiGenerateRequestDto request) {
        java.util.Map<Platforms, String> draftsMap = new java.util.HashMap<>();

        if (request.getTargetPlatforms() != null) {
            for (Platforms platform : request.getTargetPlatforms()) {
                String draft = generateOrRefineDraft(
                        request.getRawContent(),
                        platform,
                        request.getTone(),
                        request.getPreviousDraft(),
                        request.getUserFeedback());
                draftsMap.put(platform, draft);
            }
        }

        return new AiGenerateResponseDto(draftsMap);
    }
}
