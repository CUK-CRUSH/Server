package crush.myList.domain.search.service;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import crush.myList.config.EnvBean;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.music.mongo.repository.MusicRepository;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import crush.myList.domain.search.dto.MemberDto;
import crush.myList.domain.search.dto.PlaylistDto;
import crush.myList.domain.search.dto.SearchDto;
import crush.myList.domain.search.dto.VideoDto;
import crush.myList.global.enums.LimitConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static crush.myList.global.enums.LimitConstants.YOUTUBE_SEARCH_LIMIT;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {
    private final EnvBean envBean;
    private final MemberRepository memberRepository;
    private final PlaylistRepository playlistRepository;
    private final MusicRepository musicRepository;
    private static int youtubeKeyCurrentIndex = 0;

    public static int getCurrentYoutubeKey() {
        return youtubeKeyCurrentIndex + 1;
    }

    /** @param q 검색어
     * @param page 페이지
     * @return 검색 결과
     */
    public List<MemberDto> searchMembers(String q, int page) {
        Pageable pageable = PageRequest.of(page, LimitConstants.SEARCH_MEMBER_PAGE_SIZE.getLimit());
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
                        .numberOfMusics(musicRepository.countByPlaylistId(playlist.getId()))
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

    /** @param index 유튜브 API 키 인덱스
     * @return 유튜브 API 키
     */
    private String getYoutubeApiKey(final int index) throws IllegalArgumentException {
        return switch (index) {
            case 0 -> envBean.getYoutubeApiKey1();
            case 1 -> envBean.getYoutubeApiKey2();
            case 2 -> envBean.getYoutubeApiKey3();
            case 3 -> envBean.getYoutubeApiKey4();
            case 4 -> envBean.getYoutubeApiKey5();
            case 5 -> envBean.getYoutubeApiKey6();
            case 6 -> envBean.getYoutubeApiKey7();
            case 7 -> envBean.getYoutubeApiKey8();
            case 8 -> envBean.getYoutubeApiKey9();
            case 9 -> envBean.getYoutubeApiKey10();
            case 10 -> envBean.getYoutubeApiKey11();
            case 11 -> envBean.getYoutubeApiKey12();
            case 12 -> envBean.getYoutubeApiKey13();
            case 13 -> envBean.getYoutubeApiKey14();
            default -> throw new IllegalArgumentException("Youtube API 허용 인덱스 초과");
        };
    }

    /** @param search 유튜브 검색 요청 객체
     * @return 유튜브 검색 결과
     */
    private SearchListResponse requestYoutubeSearching(final YouTube.Search.List search) {
        for (int i = 0; i < envBean.getYoutubeKeyMaxSize(); i++) {
            try {
                search.setKey(getYoutubeApiKey(youtubeKeyCurrentIndex));
                return search.execute();
            } catch (GoogleJsonResponseException e) {
                if (e.getStatusCode() == 403) {
                    log.error( "[GoogleJsonResponseException] " + (youtubeKeyCurrentIndex+1)+ "번째 YouTube API Key가 만료되었습니다.");
                    youtubeKeyCurrentIndex = (youtubeKeyCurrentIndex+1) % envBean.getYoutubeKeyMaxSize();
                } else {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "[GoogleJsonResponseException] " + e.getMessage());
                }
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "[IllegalArgumentException] " + e.getMessage());
            } catch (IOException e) {
                log.error("유튜브 API 요청 중 오류가 발생했습니다.: " + e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "[IOException] Youtube API 요청 중 오류가 발생했습니다.");
            }
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Youtube API 할당량 초과로 인한 검색 실패입니다. 잠시 후 다시 시도해주세요.");
    }


    /** @param q 검색어
     * @param maxResults 최대 검색 결과 개수
     * @return 검색 결과
     */
    public List<VideoDto> searchVideos(final String q, Long maxResults) {
        try {
            // YouTube Data API에 접근할 수 있는 YouTube 클라이언트 생성
            YouTube youtube = new YouTube.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    request -> {
                    })
                    .build();

            // YouTube Data API를 사용해 동영상 검색을 위한 요청 객체 생성
            YouTube.Search.List search = youtube.search().list(Collections.singletonList("id,snippet"));
            search.setQ(q);
            search.setType(Collections.singletonList("video"));

            // 검색 결과 최대 개수 설정
            if (maxResults > YOUTUBE_SEARCH_LIMIT.getLimit()) {
                maxResults = (long) YOUTUBE_SEARCH_LIMIT.getLimit();
            }
            search.setMaxResults(maxResults);

            // 검색 요청 실행 및 응답 받아오기
            SearchListResponse searchResponse = requestYoutubeSearching(search);

            // 검색 결과에서 동영상 목록 가져오기
            List<SearchResultSnippet> searchResultSnippets = searchResponse.getItems().stream()
                    .map(SearchResult::getSnippet)
                    .toList();

            log.info("searchResultSnippets: " + searchResultSnippets.size());

            if (!searchResultSnippets.isEmpty()) {
                return searchResultSnippets.stream()
                        .map(VideoDto::of)
                        .collect(Collectors.toList());
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            log.error("유튜브 API 요청 중 오류가 발생했습니다.: " + e.getMessage());
        }
        return null;
    }
}
