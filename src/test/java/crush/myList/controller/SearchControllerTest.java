package crush.myList.controller;


import crush.myList.domain.member.entity.Member;
import crush.myList.domain.music.mongo.document.Music;
import crush.myList.domain.music.mongo.repository.MusicRepository;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.search.service.SearchService;
import crush.myList.global.enums.JwtTokenType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class SearchControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestUtil testUtil;
    @Autowired
    private MusicRepository musicRepository;

    @AfterEach
    void cleanUp() {
        musicRepository.deleteAll();
    }

    private final static String expectMember = "\"username\":\"testUser\",\"introduction\":null,\"profileImageUrl\":null";
    private final static String expectPlaylist = "\"playlistName\":\"testPlaylistName\",\"thumbnailUrl\":null";

    @Test
    @DisplayName("검색 테스트")
    public void searchTest() throws Exception {
        // given
        final String GET_API = "/api/v1/search?q=test";
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);

        // when
         MockHttpServletResponse res = mockMvc.perform(get(GET_API))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        assertThat(res.getContentAsString()).contains(expectMember);
        assertThat(res.getContentAsString()).contains(expectPlaylist);
    }

    @Test
    @DisplayName("플레이리스트 검색 테스트")
    public void searchPlaylistTest() throws Exception {
        // given
        final String GET_API = "/api/v1/search/playlist?q=test";
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        Music music = testUtil.createTestMusic(playlist);

        // when
        MockHttpServletResponse res = mockMvc.perform(get(GET_API))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].numberOfMusics").value(1))
                .andReturn().getResponse();

        // then
        assertThat(res.getContentAsString()).contains(expectPlaylist);
    }

    @Test
    @DisplayName("멤버 검색 테스트")
    public void searchMemberTest() throws Exception {
        // given
        final String GET_API = "/api/v1/search/member?q=test";
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);

        // when
         MockHttpServletResponse res = mockMvc.perform(get(GET_API))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        assertThat(res.getContentAsString()).contains(expectMember);
    }

    // 실제 유튜브 API를 호출하여 테스트하기 때문에 비활성화 처리
    @Test
    @Disabled
    @DisplayName("유튜브 검색 테스트")
    public void searchVideoTest() throws Exception {
        // given
        final String GET_API = "/api/v1/search/video?q=뉴진스&maxResults=49";
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);

        // when
        // 유튜브 API 할당량을 모두 채우기 위해 2번이 호출될 때까지 실행
        while (SearchService.getCurrentYoutubeKey() < 2) {
            MockHttpServletResponse res = mockMvc.perform(get(GET_API)
                    .header("Authorization", "Bearer " + testUtil.createAccessToken(member.getId().toString())))
                    .andReturn().getResponse();
        }
        // 한번 더 호출해서 키 변경이 잘 되었는지 확인
        MockHttpServletResponse res = mockMvc.perform(get(GET_API)
                        .header("Authorization", "Bearer " + testUtil.createAccessToken(member.getId().toString())))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // then
        System.out.println(res.getContentAsString());
    }

    @Test
    @DisplayName("검색 테스트 - 검색어 누락")
    public void searchFailTest() throws Exception {
        // given
        final String GET_API = "/api/v1/search";
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);

        // when
        MockHttpServletResponse res = mockMvc.perform(get(GET_API))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        // then
        assertThat(res.getErrorMessage()).isEqualTo("Required parameter 'q' is not present.");
    }
}
