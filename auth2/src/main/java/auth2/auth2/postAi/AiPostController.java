package auth2.auth2.postAi;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiPostController {

    private final ContentAnalysisService contentAnalysisService;
    private final AiRepurposingService aiRepurposingService;
    private final DraftRefinementService draftRefinementService;

    public AiPostController(ContentAnalysisService contentAnalysisService, 
                            AiRepurposingService aiRepurposingService, 
                            DraftRefinementService draftRefinementService) {
        this.contentAnalysisService = contentAnalysisService;
        this.aiRepurposingService = aiRepurposingService;
        this.draftRefinementService = draftRefinementService;
    }

    @PostMapping("/generate")
    public ResponseEntity<AiGenerateResponseDto> generateDrafts(@RequestBody AiGenerateRequestDto request) {
        // Step 1: Check if there is long-form raw content to analyze
        if (StringUtils.hasText(request.getRawContent())) {
            AnalyzedContent analyzed = contentAnalysisService.analyzeContent(request.getRawContent());
            
            // Augment the generation context with the AI-extracted summary
            // This prevents passing a massive 10-page text dump directly into each platform's prompt
            String augmentedContext = "Content Summary: " + analyzed.getSummary() + 
                                      "\nAdditional User Instructions: " + 
                                      (request.getUserFeedback() != null ? request.getUserFeedback() : "");
            
            // Replace the raw payload with the optimized summary before passing to the generator
            request.setRawContent(augmentedContext);
        }

        // Step 2: Generate Drafts (this now receives the concise summary instead of bloated raw text)
        AiGenerateResponseDto response = aiRepurposingService.generateDrafts(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refine")
    public ResponseEntity<GeneratedDraft> refineDraft(@RequestBody AiRefineRequestDto request) {
        // Step 3: Fast iterative refinement for a specific platform's draft
        String newDraftContent = draftRefinementService.refineDraft(
                request.getPlatform(),
                request.getPreviousDraft(),
                request.getUserFeedback(),
                request.getRawContent()
        );
        
        return ResponseEntity.ok(new GeneratedDraft(request.getPlatform(), newDraftContent));
    }
}
