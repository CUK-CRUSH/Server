package crush.myList.domain.recommendation.service;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.music.Repository.MusicRepository;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import crush.myList.domain.recommendation.dto.RecommendationDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j(topic = "RecommendationService")
@Transactional
@RequiredArgsConstructor
public class RandomRecommendationService implements Recommendation {
    private final PlaylistRepository playlistRepository;
    private final MemberRepository memberRepository;
    private final MusicRepository musicRepository;

    private static final int RECOMMENDATION_COUNT = 4;

    /**
     * 무작위 추천 플레이리스트를 조회합니다.
     *
     * @param securityMember 로그인한 사용자 정보
     * @return 추천 플레이리스트 목록
     */
    public List<RecommendationDto.Response> getRecommendation(SecurityMember securityMember) {
        // 로그인한 사용자의 플레이리스트를 제외하고 모든 플레이리스트를 조회합니다.
        Member member = null;
        if (securityMember != null) {
            member = memberRepository.findById(securityMember.getId()).orElse(null);
        }

        List<Playlist> playlists = null;

        if (member == null) {
            playlists = playlistRepository.findAll();
        } else {
            playlists = playlistRepository.findAllExceptMember(member);
        }

        // 0부터 플레이리스트의 개수의 난수 중 RECOMMENDATION_COUNT개를 추출합니다.
        Random rand = new Random();
        Set<Integer> selected = new HashSet<>();
        int n = playlists.size();

        while (selected.size() < Math.min(RECOMMENDATION_COUNT, n)) {
            int randomNum = rand.nextInt(n);
            selected.add(randomNum);
        }

        // 추천된 플레이리스트를 PlaylistDto.Response로 변환합니다.
        List<RecommendationDto.Response> responses = new LinkedList<>();

        for (int i : selected) {
            Playlist playlist = playlists.get(i);
            responses.add(RecommendationDto.Response.builder()
                            .username(playlist.getMember().getUsername())
                            .id(playlist.getId())
                            .playlistName(playlist.getName())
                            .thumbnailUrl(playlist.getImage() != null ? playlist.getImage().getUrl() : null)
                            .numberOfMusics(musicRepository.countByPlaylist(playlist))
                            .build());
        }
        return responses;
    }
}
