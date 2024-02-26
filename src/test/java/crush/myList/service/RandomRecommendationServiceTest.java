package crush.myList.service;

import crush.myList.config.security.SecurityMember;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.music.mongo.repository.MusicRepository;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import crush.myList.domain.recommendation.dto.RecommendationDto;
import crush.myList.domain.recommendation.service.RandomRecommendationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RandomRecommendationService 테스트")
public class RandomRecommendationServiceTest {
    @InjectMocks
    private RandomRecommendationService randomRecommendationService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MusicRepository musicRepository;
    @Mock
    private PlaylistRepository playlistRepository;

    @DisplayName("로그아웃 시 무작위 추천 플레이리스트 조회 테스트")
    @Test
    public void getLogoutRandomRecommendationTest() {
        // given
        given(playlistRepository.findAllByNameIsNot(eq("Untitled"))).willReturn(
                new ArrayList<>(
                        List.of(
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(1L).name("테스트1").build(),
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(2L).name("테스트2").build(),
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(3L).name("테스트3").build(),
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(4L).name("테스트4").build(),
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(5L).name("테스트5").build(),
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(6L).name("테스트6").build(),
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(7L).name("테스트7").build(),
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(8L).name("테스트8").build()
                        )
                )
        );
        given(musicRepository.countByPlaylistId(anyLong())).willReturn(10);

        // when
        List<RecommendationDto.Response> recommendation = randomRecommendationService.getRecommendation(null);

        // then
        assertThat(recommendation).hasSize(6);
        then(playlistRepository).should().findAllByNameIsNot(eq("Untitled"));
    }

    @DisplayName("로그인 시 무작위 추천 플레이리스트 조회 테스트")
    @Test
    public void getLoginRandomRecommendationTest() {
        // given
        given(memberRepository.findById(any())).willReturn(
                java.util.Optional.of(
                        Member.builder().id(1L).username("test").build()
                )
        );
        given(playlistRepository.findAllByMemberIsNotAndNameIsNot(any(), eq("Untitled"))).willReturn(
                new ArrayList<>(
                        List.of(
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(1L).name("테스트1").build(),
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(2L).name("테스트2").build(),
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(3L).name("테스트3").build(),
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(4L).name("테스트4").build(),
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(5L).name("테스트5").build(),
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(6L).name("테스트6").build(),
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(7L).name("테스트7").build(),
                                Playlist.builder().member(Member.builder().id(1L).username("Test").build()).id(8L).name("테스트8").build()
                        )
                )
        );
        given(musicRepository.countByPlaylistId(anyLong())).willReturn(10);

        // when
        List<RecommendationDto.Response> recommendation = randomRecommendationService.getRecommendation(
                SecurityMember.builder().id(1L).username("test").build()
        );

        // then
        assertThat(recommendation).hasSize(6);
        then(playlistRepository).should().findAllByMemberIsNotAndNameIsNot(any(), eq("Untitled"));
    }
}
