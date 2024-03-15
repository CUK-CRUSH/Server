package crush.myList.domain.member.dto;

import crush.myList.domain.viewcounting.entity.View;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    @Schema(description = "회원 id", example = "1")
    private Long id;
    @Schema(description = "oauth2 id", example = "google:1234567890")
    private String oauth2id;
    @Schema(description = "유저네임", example = "crush")
    private String username;
    @Schema(description = "이름", example = "crush")
    private String name;
    @Schema(description = "소개", example = "crush is my life")
    private String introduction;
    @Schema(description = "프로필 이미지 url", example = "https://storage.googleapis.com/mylist-image_v2/img/cdf49167-bf8d-4c36-bf2e-cb7fe7c7c7b7")
    private String profileImageUrl;
    @Schema(description = "배경 이미지 url", example = "https://storage.googleapis.com/mylist-image_v2/img/3e3e3e3e-3e3e-3e3e-3e3e-3e3e3e3e3e3e")
    private String backgroundImageUrl;
    private View view;
}
