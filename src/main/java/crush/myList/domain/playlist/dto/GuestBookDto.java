package crush.myList.domain.playlist.dto;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class GuestBookDto {
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post {
        @NotBlank
        private String content;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        @NotNull
        @Schema(example = "{'id': 1, 'username': 'username', 'name': 'name', 'profileImageUrl': 'profileImageUrl'}", implementation = MemberDto.class)
        private MemberDto member;
        @Schema(example = "content")
        @NotBlank
        private String content;
        @Schema(example = "2021-08-01 00:00:00")
        @NotBlank
        private String createdDate;
        @Schema(example = "2021-08-01 00:00:00")
        @NotBlank
        private String modifiedDate;

        @Getter
        @Setter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class MemberDto {
            private Long id;
            private String username;
            private String name;
            private String profileImageUrl;
        }
    }
}
