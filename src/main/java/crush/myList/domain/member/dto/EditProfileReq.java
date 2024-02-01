package crush.myList.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EditProfileReq", description = "프로필 수정 요청", type = "multipartForm")
public class EditProfileReq {
    @Schema(example = "crush", nullable = true)
    private String username;
    @Schema(example = "crush is my life", nullable = true)
    private String introduction;
    @Schema(type = "string", format = "binary", example = "profile.jpg", nullable = true)
    private MultipartFile profileImage;
    @Schema(type = "string", format = "binary", example = "background.jpg", nullable = true)
    private MultipartFile backgroundImage;
    @Schema(type = "boolean", example = "true", description = "프로필 이미지 삭제 여부", nullable = true)
    private Boolean deleteProfileImage;
    @Schema(type = "boolean", example = "true", description = "배경 이미지 삭제 여부", nullable = true)
    private Boolean deleteBackgroundImage;
}
