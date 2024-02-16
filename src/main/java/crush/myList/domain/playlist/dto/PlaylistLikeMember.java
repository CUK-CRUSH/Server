package crush.myList.domain.playlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PlaylistLikeMember", description = "플레이리스트 좋아요 누른 사용자 정보입니다.")
public class PlaylistLikeMember {
    @Schema(name = "id", description = "사용자 ID", example = "1")
    private Long id;
    @Schema(name = "username", description = "사용자 닉네임", example = "crush")
    private String username;
    @Schema(name = "introduction", description = "사용자 소개", example = "안녕하세요!")
    private String introduction;
    @Schema(name = "profileImageUrl", description = "사용자 프로필 이미지 URL")
    private String profileImageUrl;
}