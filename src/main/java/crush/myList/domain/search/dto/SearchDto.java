package crush.myList.domain.search.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {
    @Schema(name = "members", description = "사용자 검색 결과입니다.")
    private List<MemberDto> members;
    @Schema(name = "musics", description = "음악 검색 결과입니다.")
    private List<PlaylistDto> playlists;
}
