package crush.myList.domain.member.dto;

import lombok.*;

import java.util.HashMap;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerspectiveDtoRequest {
    private Comment comment;
    private List<String> languages;
    private RequestedAttributes requestedAttributes;

    @Builder
    @Getter
    public static class Comment {
        private String text;
    }

    @Builder
    @Getter
    public static class RequestedAttributes {
        private Toxicity TOXICITY;
    }

    @Builder
    @Getter
    @Setter
    public static class Toxicity { // 독성
        private HashMap<String, String> dummy;
    }
}