package crush.myList.domain.playlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

public class PlaylistDto {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req {
        @Schema(name = "playlistName", description = "플레이리스트 이름입니다.")
        @NotEmpty
        private String playlistName;
    }
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Res {
        @Schema(name = "playlistName", description = "플레이리스트 이름입니다.")
        @NotEmpty
        private String playlistName;

        @Schema(name = "thumnailUrl", description = "썸네일 이미지 주소입니다.")
        private String thumbnailUrl;

        @Schema(name = "numberOfMusics", description = "플레이리스트 음악 개수입니다.")
        private Long numberOfMusics;
    }
}