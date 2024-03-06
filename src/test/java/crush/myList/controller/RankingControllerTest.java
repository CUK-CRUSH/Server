package crush.myList.controller;

import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.entity.PlaylistLike;
import crush.myList.domain.ranking.service.DailyMemberRankingService;
import crush.myList.domain.ranking.service.DailyPlaylistRankingService;
import crush.myList.global.enums.JwtTokenType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class RankingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private TestUtil testUtil;
    @Autowired
    private DailyPlaylistRankingService dailyPlaylistRankingService;
    @Autowired
    private DailyMemberRankingService dailyMemberRankingService;

    @DisplayName("랭킹 조회 테스트 - 업데이트 후 조회")
    @Test
    public void getRankingTest() throws Exception {
        // given
        testUtil.clearAll();
        Member member1 = testUtil.createTestMember("test111");
        Member member2 = testUtil.createTestMember("test222");

        Playlist playlist1 = testUtil.createTestPlaylist(member1);
        Playlist playlist2 = testUtil.createTestPlaylist(member2);

        PlaylistLike playlistLike1 = testUtil.createTestPlaylistLike(member1, playlist1);
        PlaylistLike playlistLike2 = testUtil.createTestPlaylistLike(member1, playlist2);
        PlaylistLike playlistLike3 = testUtil.createTestPlaylistLike(member2, playlist1);
        PlaylistLike playlistLike4 = testUtil.createTestPlaylistLike(member2, playlist2);

        dailyPlaylistRankingService.updateRanking();
        dailyMemberRankingService.updateRanking();

        final String PLAYLIST_GET_API = "/api/v1/ranking/daily/playlists";
        final String USER_GET_API = "/api/v1/ranking/daily/users";

        // when
        // 업데이트
        dailyPlaylistRankingService.updateRanking();
        dailyMemberRankingService.updateRanking();

        // 조회
        mockMvc.perform(get(PLAYLIST_GET_API)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member1.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.message").value("일간 플레이리스트 랭킹 조회 성공"));

        mockMvc.perform(get(USER_GET_API)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member1.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.message").value("일간 유저 랭킹 조회 성공"));
    }
}