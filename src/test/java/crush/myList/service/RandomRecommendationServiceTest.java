package crush.myList.service;

import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.music.Repository.MusicRepository;
import crush.myList.domain.playlist.dto.PlaylistDto;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import crush.myList.domain.recommendation.service.RandomRecommendationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;

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

    @DisplayName("무작위 추천 플레이리스트 조회 테스트")
    @Test
    public void getRandomRecommendationTest() {
        // given
        given(playlistRepository.findAll()).willReturn(
                List.of(
                        Playlist.builder().id(1L).name("테스트1").build(),
                        Playlist.builder().id(2L).name("테스트2").build(),
                        Playlist.builder().id(3L).name("테스트3").build(),
                        Playlist.builder().id(4L).name("테스트4").build(),
                        Playlist.builder().id(5L).name("테스트5").build(),
                        Playlist.builder().id(6L).name("테스트6").build(),
                        Playlist.builder().id(7L).name("테스트7").build(),
                        Playlist.builder().id(8L).name("테스트8").build()
                )
        );
        given(musicRepository.countByPlaylist(any(Playlist.class))).willReturn(10L);

        // when
        List<PlaylistDto.Response> recommendation = randomRecommendationService.getRecommendation(null);

        // then
        assertThat(recommendation).hasSize(4);
        then(playlistRepository).should().findAll();
    }
}
