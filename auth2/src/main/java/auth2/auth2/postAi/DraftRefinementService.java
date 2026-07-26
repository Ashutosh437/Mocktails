package auth2.auth2.postAi;

import auth2.auth2.Posts.Platforms;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DraftRefinementService {

    private final ChatClient chatClient;

    public DraftRefinementService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String refineDraft(Platforms platform, String previousDraft, String userFeedback, String rawContent) {
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are an expert social media manager. Modify the following draft for ")
                .append(platform).append(".\n");
        promptBuilder.append("User Feedback / Instructions: ").append(userFeedback).append("\n");

        if (StringUtils.hasText(rawContent)) {
            promptBuilder.append("Original Source Context: ").append(rawContent).append("\n");
        }

        promptBuilder.append("Previous Draft to modify: \n").append(previousDraft).append("\n");
        promptBuilder.append("\nReturn ONLY the exact text for the post, with no conversational filler.");

        return chatClient.prompt().user(promptBuilder.toString()).call().content();
    }
}
