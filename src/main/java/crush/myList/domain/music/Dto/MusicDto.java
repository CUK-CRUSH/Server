package crush.myList.domain.music.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class MusicDto {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "음악 추가 요청", description = "postRequest")
    public static class PostRequest {
        @Schema(name = "order", description = "음악 순서입니다.")
        private Integer order;

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
//    @Schema(name = "음악 수정 요청", description = "patchRequest")
//    public static class PatchRequest {
//        @Schema(name = "title", description = "음악 제목입니다.")
//        private String title;
//
//        @Schema(name = "artist", description = "아티스트 이름입니다.")
//        private String artist;
//
//        @Schema(name = "url", description = "음악 링크 이름입니다.")
//        private String url;
//    }

    @Getter
    @Setter
    @Schema(name = "음악 수정 요청", description = "patchRequestV1")
    public static class PatchRequestV1 {
        @NotBlank
        @Schema(name = "musicId", description = "음악 ID입니다.")
        private String musicId;

        @Schema(name = "musicOrder", description = "음악 순서입니다.")
        private Integer musicOrder;

        @Schema(name = "title", description = "음악 제목입니다.")
        private String title;

        @Schema(name = "artist", description = "아티스트 이름입니다.")
        private String artist;

        @Schema(name = "url", description = "음악 링크 이름입니다.")
        private String url;
    }

    @Getter
    @Setter
    @Schema(name = "음악 삭제 요청", description = "deleteRequest")
    public static class DeleteRequest {
        @NotBlank
        @Schema(name = "musicId", description = "음악 ID입니다.")
        private String musicId;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "음악 응답", description = "result")
    public static class Result {
        @Schema(name = "id", description = "음악 ID입니다.")
        @NotNull
        private String id;

        @Schema(name = "order", description = "음악 순서입니다.")
        private Integer order;

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
}
