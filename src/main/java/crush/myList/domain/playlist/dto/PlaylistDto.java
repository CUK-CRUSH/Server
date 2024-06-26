package crush.myList.domain.playlist.dto;

import crush.myList.domain.viewcounting.dto.ViewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class PlaylistDto {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "PlaylistPostRequest", description = "플레이리스트 추가 요청", type = "multipartForm")
    public static class PostRequest {
        @Schema(name = "playlistName", description = "플레이리스트 이름입니다.")
        private String playlistName;

        @Schema(name = "titleImage", description = "플레이리스트 이미지입니다.", type = "string", format = "binary")
        private MultipartFile titleImage;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "PlaylistPatchRequest", description = "플레이리스트 수정 요청", type = "multipartForm")
    public static class PatchRequest {
        @Schema(name = "playlistName", description = "플레이리스트 이름입니다.")
        private String playlistName;

        @Schema(name = "titleImage", description = "플레이리스트 이미지입니다.", type = "string", format = "binary")
        private MultipartFile titleImage;

        @Schema(name = "deletePlaylistImage", description = "플레이리스트 이미지 삭제 여부입니다.", type = "boolean")
        private Boolean deletePlaylistImage;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "PlaylistResponse", description = "플레이리스트 정보 응답입니다.")
    public static class Response {
        @Schema(name = "id", description = "플레이리스트 ID입니다.")
        @NotNull
        private Long id;

        @Schema(name = "playlistName", description = "플레이리스트 이름입니다.")
        @NotEmpty
        private String playlistName;

        @Schema(name = "username", description = "플레이리스트 생성자 이름입니다.")
        @NotEmpty
        private String username;

        @Schema(name = "thumbnailUrl", description = "썸네일 이미지 주소입니다.")
        private String thumbnailUrl;

        @Schema(name = "numberOfMusics", description = "플레이리스트 음악 개수입니다.")
        private Integer numberOfMusics;

        @Schema(name = "likeCount", description = "플레이리스트 좋아요 개수입니다.")
        private Integer likeCount;

        @Schema(name = "isLike", description = "플레이리스트 좋아요 여부입니다.")
        private Boolean isLike;

        @Schema(name = "view", description = "플레이리스트 조회수입니다.")
        private ViewDto view;
    }
}