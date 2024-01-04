package crush.myList.domain.playlist.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistDto {
    @Schema(name = "playlist_name", description = "플레이리스트 이름입니다.")
    @NotEmpty
    private String playlistName;

    @Schema(name = "thumnail_url", description = "썸네일 이미지 주소입니다.")
    private String thumbnailUrl;

    @Schema(name = "number_of_musics", description = "플레이리스트 음악 개수입니다.")
    private Long numberOfMusics;
}
