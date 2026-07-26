package auth2.auth2.postAi;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ContentAnalysisService {

    private final ChatClient chatClient;

    public ContentAnalysisService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public AnalyzedContent analyzeContent(String rawContent) {
        String prompt = "Analyze the following content and provide a summary. Return only the summary text.\n" + rawContent;
        String response = chatClient.prompt().user(prompt).call().content();
        
        AnalyzedContent result = new AnalyzedContent();
        result.setSummary(response);
        return result;
    }
}
