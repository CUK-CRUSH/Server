package crush.myList.domain.recommendation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class RecommendationDto {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "RecommendationResponse", description = "추천 플레이리스트 응답입니다.")
    public static class Response {
        @Schema(name = "username", description = "플레이리스트 소유자 이름입니다.")
        @NotEmpty
        private String username;

        @Schema(name = "id", description = "플레이리스트 ID입니다.")
        @NotNull
        private Long id;

        @Schema(name = "playlistName", description = "플레이리스트 이름입니다.")
        @NotEmpty
        private String playlistName;

        @Schema(name = "thumbnailUrl", description = "썸네일 이미지 주소입니다.")
        private String thumbnailUrl;

        @Schema(name = "numberOfMusics", description = "플레이리스트 음악 개수입니다.")
        private Long numberOfMusics;
    }
}
