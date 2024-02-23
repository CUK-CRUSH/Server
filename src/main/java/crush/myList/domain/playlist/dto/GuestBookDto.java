package crush.myList.domain.playlist.dto;

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
        private MemberDto member;
        @NotBlank
        private String content;
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
