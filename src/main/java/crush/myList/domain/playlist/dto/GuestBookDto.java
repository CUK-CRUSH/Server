package crush.myList.domain.playlist.dto;

import jakarta.validation.constraints.NotBlank;
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
        @NotBlank
        private String username;
        @NotBlank
        private String content;
        @NotBlank
        private String modifiedDate;
    }
}
