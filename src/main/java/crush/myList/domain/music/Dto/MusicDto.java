package crush.myList.domain.music.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

public class MusicDto {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req {
        @Schema(name = "title", description = "음악 제목입니다.")
        @NotEmpty
        private String title;

        @Schema(name = "artist", description = "아티스트 이름입니다.")
        @NotEmpty
        private String artist;

        @Schema(name = "url", description = "음악 링크 이름입니다.")
        @NotBlank
        private String url;
    }

//    @Getter
//    @Setter
//    @Builder
//    @NoArgsConstructor
//    @AllArgsConstructor
//    public static class Res {
//
//    }
}
