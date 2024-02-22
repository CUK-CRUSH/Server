package crush.myList.domain.search.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchDto {
    private List<MemberDto> members;
    private List<PlaylistDto> playlists;
}