package crush.myList.controller;

import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.music.entity.Music;
import crush.myList.domain.playlist.dto.GuestBookDto;
import crush.myList.domain.playlist.entity.GuestBook;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.entity.PlaylistLike;
import crush.myList.global.enums.JwtTokenType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class PlaylistControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private TestUtil testUtil;

    @DisplayName("사용자의 모든 플레이리스트 조회 테스트")
    @Test
    public void viewAllPlaylistsTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        Music music = testUtil.createTestMusic(playlist);

        final String api = "/api/v1/playlist/user/" + member.getUsername();

        // when
        testReporter.publishEntry(
                mockMvc.perform(get(api))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data[0].id").value(playlist.getId()))
                        .andExpect(jsonPath("$.data[0].username").value(member.getUsername()))
                        .andReturn().getResponse().getContentAsString()
        );
    }

    @DisplayName("플레이리스트 단일 조회 테스트 - 오프라인")
    @Test
    public void viewPlaylistTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        Music music = testUtil.createTestMusic(playlist);

        final String api = "/api/v1/playlist/" + playlist.getId();

        // when
        MockHttpServletResponse response = mockMvc.perform(get(api))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(response.getContentAsString()).contains("likeCount\":0");
    }

    @DisplayName("플레이리스트 단일 조회 테스트 - 온라인 && 본인이 좋아요한 플레이리스트")
    @Test
    public void viewPlaylistOnlineTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        PlaylistLike playlistLike = testUtil.createTestPlaylistLike(member, playlist);
        Music music = testUtil.createTestMusic(playlist);
        String accessToken = jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN);

        final String api = "/api/v1/playlist/" + playlist.getId();

        // when
        MockHttpServletResponse response = mockMvc.perform(get(api)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(response.getContentAsString()).contains("isLike\":true");
    }

    @DisplayName("플레이리스트 단일 조회 테스트 - 온라인 && 본인이 좋아요하지 않은 플레이리스트")
    @Test
    public void viewPlaylistOnlineNotLikedTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Member otherMember = testUtil.createTestMember("otherUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        PlaylistLike playlistLike = testUtil.createTestPlaylistLike(member, playlist);
        Music music = testUtil.createTestMusic(playlist);
        String accessToken = jwtTokenProvider.createToken(otherMember.getId().toString(), JwtTokenType.ACCESS_TOKEN);

        final String api = "/api/v1/playlist/" + playlist.getId();

        // when
        MockHttpServletResponse response = mockMvc.perform(get(api)
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse();
        assertThat(response.getContentAsString()).contains("likeCount\":1");
    }

    @DisplayName("플레이리스트 단일 조회 테스트 - 실패")
    @Test
    public void viewPlaylistFailTest(TestReporter testReporter) throws Exception {
        // given
        testUtil.deletePlaylist(1L);
        final String api = "/api/v1/playlist/1";

        // when
        testReporter.publishEntry(
                mockMvc.perform(get(api))
                        .andExpect(status().isNotFound())
                        .andReturn().getResponse().toString()
        );
    }

    @DisplayName("플레이리스트 좋아요 조회 테스트")
    @Test
    public void viewPlaylistLikesTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);

        for (int i = 0; i < 20; i++) {
            Member newMember = testUtil.createTestMember("testUser" + i);
            testUtil.createTestPlaylistLike(newMember, playlist);
        }

        final String api = "/api/v1/playlist/" + playlist.getId() + "/like";

        // when
        assertThat(
                mockMvc.perform(get(api))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.length()").value(15))
                        .andReturn().getResponse().getContentAsString()
        ).contains("testUser6");
    }

    @DisplayName("플레이리스트 좋아요 추가 테스트")
    @Test
    public void likePlaylistTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        Music music = testUtil.createTestMusic(playlist);

        final String api = "/api/v1/playlist/" + playlist.getId() + "/like";

        // when
        testReporter.publishEntry(
                mockMvc.perform(
                        MockMvcRequestBuilders.post(api)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );
    }

    @DisplayName("플레이리스트 좋아요 취소 테스트")
    @Test
    public void unlikePlaylistTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        Music music = testUtil.createTestMusic(playlist);
        PlaylistLike playlistLike = testUtil.createTestPlaylistLike(member, playlist);

        final String api = "/api/v1/playlist/" + playlist.getId() + "/like";

        // when
        testReporter.publishEntry(
                mockMvc.perform(
                        MockMvcRequestBuilders.delete(api)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );
    }

    @DisplayName("플레이리스트 생성 테스트")
    @Test
    @Disabled
    public void createPlaylistTest(TestReporter testReporter) throws Exception {
        // given
        final String api = "/api/v1/playlist";
        Member member = testUtil.createTestMember("testUser");

        MockMultipartFile imageFile = testUtil.createTestImage("titleImage");

        // when
        testReporter.publishEntry(
                mockMvc.perform(
                        multipart(api)
                                .file(imageFile)
                                .param("playlistName", "testPlaylist")
                                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.playlistName").value("testPlaylist"))
                        .andReturn().getResponse().getContentAsString()
        );
    }

    @DisplayName("플레이리스트 수정 테스트")
    @Test
    @Disabled
    public void updatePlaylistTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        MockMultipartFile imageFile = testUtil.createTestImage("titleImage");

        final String api = "/api/v1/playlist/" + playlist.getId();

        // when
        testReporter.publishEntry(
                mockMvc.perform(
                        multipart(HttpMethod.PATCH, api)
                                .file(imageFile)
                                .param("playlistName", "testPlaylist")
                                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );
    }

    @DisplayName("플레이리스트 삭제 테스트")
    @Test
    public void deletePlaylistTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);

        final String api = "/api/v1/playlist/" + playlist.getId();

        testReporter.publishEntry(
                mockMvc.perform(
                        MockMvcRequestBuilders.delete(api)
                                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );
    }

    @DisplayName("플레이리스트 이미지 삭제 테스트 (Deprecated)")
    @Test
    @Disabled
    public void deletePlaylistImageTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        MockMultipartFile imageFile = testUtil.createTestImage("titleImage");

        final String POST_API = "/api/v1/playlist";

        testReporter.publishEntry(
                mockMvc.perform(
                                multipart(POST_API)
                                        .file(imageFile)
                                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                                        .contentType(MediaType.MULTIPART_FORM_DATA))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.playlistName").value("Untitled"))
                        .andReturn().getResponse().getContentAsString()
        );

        final String DELETE_IMAGE_API = "/api/v1/playlist/" + playlist.getId() + "/image";

        testReporter.publishEntry(
                mockMvc.perform(
                                MockMvcRequestBuilders.delete(DELETE_IMAGE_API)
                                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );
    }

    @DisplayName("플레이리스트 PATCH 메소드에서 이미지 삭제 테스트")
    @Test
    @Disabled
    public void deletePlaylistImageWithPatchTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        MockMultipartFile imageFile = testUtil.createTestImage("titleImage");

        final String UPDATE_API = "/api/v1/playlist/" + playlist.getId();

        testReporter.publishEntry(
                mockMvc.perform(
                                multipart(HttpMethod.PATCH, UPDATE_API)
                                        .file(imageFile)
                                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                                        .contentType(MediaType.MULTIPART_FORM_DATA))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.playlistName").value("testPlaylistName"))
                        .andExpect(jsonPath("$.data.thumbnailUrl").isNotEmpty())
                        .andReturn().getResponse().getContentAsString()
        );

        // when
        // 이미지 삭제 안할 때
        testReporter.publishEntry(
                mockMvc.perform(
                                MockMvcRequestBuilders.patch(UPDATE_API)
                                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .param("deletePlaylistImage", "false")
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.thumbnailUrl").isNotEmpty())
                        .andReturn().getResponse().getContentAsString()
        );

        // 이미지 삭제 할 때
        testReporter.publishEntry(
                mockMvc.perform(
                                MockMvcRequestBuilders.patch(UPDATE_API)
                                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .param("deletePlaylistImage", "true")
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.thumbnailUrl").isEmpty())
                        .andReturn().getResponse().getContentAsString()
        );
    }

    @DisplayName("타인의 플레이리스트 수정")
    @Test
    @Disabled
    public void updateOthersPlaylistTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Member otherMember = testUtil.createTestMember("otherUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        MockMultipartFile imageFile = testUtil.createTestImage("titleImage");

        final String api = "/api/v1/playlist/" + playlist.getId();

        // when
        testReporter.publishEntry(
                mockMvc.perform(
                        multipart(HttpMethod.PATCH, api)
                                .file(imageFile)
                                .param("playlistName", "testPlaylist")
                                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(otherMember.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                        .andExpect(status().isForbidden())
                        .andReturn().getResponse().toString()
        );
    }

    @Test
    @DisplayName("플레이리스트 방명록 조회")
    public void getGuestBooksTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Member otherUser = testUtil.createTestMember("otherUser");

        Playlist playlist = testUtil.createTestPlaylist(member);

        for (int i = 0; i < 20; i++) {
            testUtil.createTestGuestBook(otherUser, playlist);
        }

        final String api = "/api/v1/playlist/" + playlist.getId() + "/guestbook";

        // when
        MockHttpServletResponse res = mockMvc.perform(get(api))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.length()").value(15))
                        .andReturn().getResponse();

        // then
        assertThat(res.getContentAsString()).contains("testContent");
        System.out.println(res.getContentAsString());
    }

    @Test
    @DisplayName("플레이리스트 방명록 추가")
    public void addGuestBookTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        final String api = "/api/v1/playlist/" + playlist.getId() + "/guestbook";
        final GuestBookDto.Post post = GuestBookDto.Post.builder().content("testContent").build();

        // when
        MockHttpServletResponse res = mockMvc.perform(
                        MockMvcRequestBuilders.post(api)
                                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testUtil.toJson(post)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.member.username").value(member.getUsername()))
                        .andReturn().getResponse();

        // then
        assertThat(res.getContentAsString()).contains("testContent");
        System.out.println(res.getContentAsString());
    }

    @Test
    @DisplayName("플레이리스트 방명록 수정")
    public void updateGuestBookTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        GuestBook guestBook = testUtil.createTestGuestBook(member, playlist);
        final String api = "/api/v1/playlist/" + playlist.getId() + "/guestbook/" + guestBook.getId();
        final GuestBookDto.Post post = GuestBookDto.Post.builder().content("updatedContent").build();

        // when
        MockHttpServletResponse res = mockMvc.perform(
                        MockMvcRequestBuilders.patch(api)
                                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(testUtil.toJson(post)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.member.username").value(member.getUsername()))
                        .andReturn().getResponse();

        // then
        assertThat(res.getContentAsString()).contains("updatedContent");
        System.out.println(res.getContentAsString());
    }

    @Test
    @DisplayName("플레이리스트 방명록 삭제")
    public void deleteGuestBookTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        GuestBook guestBook = testUtil.createTestGuestBook(member, playlist);
        final String api = "/api/v1/playlist/" + playlist.getId() + "/guestbook/" + guestBook.getId();

        // when
        mockMvc.perform(
                        MockMvcRequestBuilders.delete(api)
                                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                        .andExpect(status().isOk());
    }
}
