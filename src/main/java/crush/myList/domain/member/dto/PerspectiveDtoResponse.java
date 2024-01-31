package crush.myList.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerspectiveDtoResponse {
    private AttributeScores attributeScores;
    private List<String> languages;
    private List<String> detectedLanguages;

    @Getter
    public static class AttributeScores {
        @JsonProperty("TOXICITY")   // 대문자 고정
        private Toxicity TOXICITY;
    }

    @Getter
    public static class Toxicity {
        private SummaryScore summaryScore;
    }

    @Getter
    public static class SummaryScore {
        private Double value;
    }
}
