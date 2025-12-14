package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.model.activity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ActivityAiService {
    private final GeminiService geminiService ;

    public Recommendation generateRecommendation(activity activity){
        String prompt = createPromptForActivity(activity);
        String aiResponse = geminiService.getRecommendations(prompt) ;

        return processAiResponse(activity,aiResponse);
        }

    private Recommendation processAiResponse(activity activity, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);
            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .get("parts")
                    .get(0)
                    .path("text") ;

            String raw = textNode.asText();

            // Extract JSON substring between first '{' and last '}'
            int start = raw.indexOf('{');
            int end = raw.lastIndexOf('}');
            if (start == -1 || end == -1 || start >= end) {
                throw new IllegalStateException("AI response does not contain valid JSON");
            }

            String jsonContent = raw.substring(start, end + 1).trim();

            log.info("Response from Ai JSON: {}", jsonContent);

            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");
            StringBuilder fullAnalysis = new StringBuilder();
            addAnalysisSection(fullAnalysis,analysisNode,"overall","Overall: ");
            addAnalysisSection(fullAnalysis,analysisNode,"pace","Pace : ");
            addAnalysisSection(fullAnalysis,analysisNode,"heartRate","Heart Race: ");
            addAnalysisSection(fullAnalysis,analysisNode,"caloriesBurned","Calories Burned: ");

            List<String> improvements = extractImprovements(analysisJson.path("improvements")) ;
            List<String> suggestions = extractSuggestions(analysisJson.path("suggestions")) ;
            List<String> safety  = extractSafety(analysisJson.path("safety")) ;

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .type(activity.getType().toString())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build() ;

        } catch (Exception e) {
            log.error("Failed to process AI response", e);

            return createDefaultRecommendation(activity) ;
        }


    }

    private Recommendation createDefaultRecommendation(activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getType().toString())
                .recommendation("Unable to Generate analysis")
                .improvements(Collections.singletonList("Continue With Current routine"))
                .suggestions(Collections.singletonList("Consult a trainer"))
                .safety(Collections.singletonList("Maintain default safety"))
                .createdAt(LocalDateTime.now())
                .build() ;
    }

    private List<String> extractSafety(JsonNode safetyNode) {
        List<String> safety = new ArrayList<>();
        if(safetyNode.isArray()){
            safetyNode.forEach(item -> safety.add(item.asText()));


        }
        return safety.isEmpty() ? Collections.singletonList("No safety provided") :
                safety;
    }

    private List<String> extractSuggestions(JsonNode suggestionsNode) {
        List<String> suggestions = new ArrayList<>();
        if (suggestionsNode.isArray()) {
            suggestionsNode.forEach(suggestion -> {
                String workout = suggestion.path("workout").asText();
                String description = suggestion.path("description").asText();
                suggestions.add(String.format("%s: %s", workout, description));
            });
        }
        return suggestions.isEmpty() ? Collections.singletonList("No suggestions provided") :
                suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements = new ArrayList<>();
        if(improvementsNode.isArray()){
            improvementsNode.forEach(improvement->{
                String area = improvement.path("area").asText();
                String recommendation = improvement.path("recommendation").asText();
                improvements.add(String.format("%s: %s",area,recommendation));
            });
        }
        return improvements.isEmpty() ? Collections.singletonList("No improvements provided") :
                improvements ;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix).append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(activity activity) {
        return String.format("""
        Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
        {
          "analysis": {
            "overall": "Overall analysis here",
            "pace": "Pace analysis here",
            "heartRate": "Heart rate analysis here",
            "caloriesBurned": "Calories analysis here"
          },
          "improvements": [
            {
              "area": "Area name",
              "recommendation": "Detailed recommendation"
            }
          ],
          "suggestions": [
            {
              "workout": "Workout name",
              "description": "Detailed workout description"
            }
          ],
          "safety": [
            "Safety point 1",
            "Safety point 2"
          ]
        }

        Analyze this activity:
        Activity Type: %s
        Duration: %d minutes
        Calories Burned: %d
        Additional Metrics: %s
        
        Provide detailed analysis focusing on performance, improvements, next workout suggestions, and safety guidelines.
        Ensure the response follows the EXACT JSON format shown above.
        """,
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );

    }
}
