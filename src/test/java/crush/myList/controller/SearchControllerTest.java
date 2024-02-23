package crush.myList.controller;


import crush.myList.domain.member.entity.Member;
import crush.myList.domain.music.entity.Music;
import crush.myList.domain.playlist.entity.Playlist;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
