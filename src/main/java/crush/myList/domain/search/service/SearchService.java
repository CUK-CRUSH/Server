package crush.myList.domain.search.service;

import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import crush.myList.domain.search.dto.MemberDto;
import crush.myList.domain.search.dto.PlaylistDto;
import crush.myList.domain.search.dto.SearchDto;
import crush.myList.global.enums.LimitConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {
    private final MemberRepository memberRepository;
    private final PlaylistRepository playlistRepository;

    /** @param q 검색어
     * @param page 페이지
     * @return 검색 결과
     */
    public List<MemberDto> searchMembers(String q, int page) {
        Pageable pageable = PageRequest.of(page, LimitConstants.SEARCH_PLAYLIST_PAGE_SIZE.getLimit());
        return memberRepository.findByUsernameContaining(q, pageable)
                .stream()
                .map(member -> MemberDto.builder()
                        .id(member.getId())
                        .username(member.getUsername())
                        .introduction(member.getIntroduction())
                        .profileImageUrl(member.getProfileImage() != null ? member.getProfileImage().getUrl() : null)
                        .build())
                .toList();
    }

    /** @param q 검색어
     * @param page 페이지
     * @return 검색 결과
     */
    public List<PlaylistDto> searchPlaylists(String q, int page) {
        Pageable pageable = PageRequest.of(page, LimitConstants.SEARCH_PLAYLIST_PAGE_SIZE.getLimit());
        return playlistRepository.findByNameContaining(q, pageable)
                .stream()
                .map(playlist -> PlaylistDto.builder()
                        .id(playlist.getId())
                        .username(playlist.getMember().getUsername())
                        .playlistName(playlist.getName())
                        .thumbnailUrl(playlist.getImage() != null ? playlist.getImage().getUrl() : null)
                        .build())
                .toList();
    }

    /** @param q 검색어
     * @return 검색 결과
     * @apiNote 검색 결과를 반환합니다. 검색 결과는 멤버와 플레이리스트 첫번째 페이징으로 구성됩니다.
     */
    public SearchDto search(String q) {
        return SearchDto.builder()
                .members(searchMembers(q, 0))
                .playlists(searchPlaylists(q, 0))
                .build();
    }
}
