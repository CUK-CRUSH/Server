package crush.myList.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EditProfileReq", description = "프로필 수정 요청", type = "multipartForm")
public class EditProfileReq {
    @Schema(example = "crush")
    private String username;
    @Schema(example = "crush is my life")
    private String introduction;
    @Schema(type = "string", format = "binary", example = "profile.jpg")
    private MultipartFile profileImage;
    @Schema(type = "string", format = "binary", example = "background.jpg")
    private MultipartFile backgroundImage;
}
