package crush.myList.domain.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "PlaylistDto", description = "플레이리스트 검색 결과입니다.")
public class PlaylistDto {
    @Schema(name = "id", description = "플레이리스트 ID입니다.")
    @NotNull
    private Long id;

    @Schema(name = "username", description = "플레이리스트 소유자 이름입니다.")
    @NotBlank
    private String username;

    @Schema(name = "playlistName", description = "플레이리스트 이름입니다.")
    @NotBlank
    private String playlistName;

    @Schema(name = "thumbnailUrl", description = "썸네일 이미지 주소입니다.")
    private String thumbnailUrl;

    @Schema(name = "numberOfMusics", description = "플레이리스트에 포함된 음악 수입니다.")
    private Integer numberOfMusics;
}