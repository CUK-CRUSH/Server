package crush.myList.controller;

import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.entity.Role;
import crush.myList.domain.member.enums.RoleName;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.member.repository.RoleRepository;
import crush.myList.domain.music.Entity.Music;
import crush.myList.domain.music.Repository.MusicRepository;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.repository.PlaylistRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class PlaylistControllerTest extends TestHelper {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("사용자의 모든 플레이리스트 조회 테스트")
    @Test
    public void viewAllPlaylistsTest(TestReporter testReporter) throws Exception {
        // given
        Member member = createTestMember();
        Playlist playlist = createTestPlaylist(member);
        Music music = createTestMusic(playlist);

        final String api = "/api/v1/playlist/user/" + member.getUsername();

        // when
        testReporter.publishEntry(
                mockMvc.perform(get(api))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );
    }

    @DisplayName("플레이리스트 단일 조회 테스트")
    @Test
    public void viewPlaylistTest(TestReporter testReporter) throws Exception {
        // given
        Member member = createTestMember();
        Playlist playlist = createTestPlaylist(member);
        Music music = createTestMusic(playlist);

        final String api = "/api/v1/playlist/" + playlist.getId();

        // when
        testReporter.publishEntry(
                mockMvc.perform(get(api))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );
    }

    @DisplayName("플레이리스트 생성 테스트")
    @Test
    public void createPlaylistTest(TestReporter testReporter) throws Exception {
        // given
        final String api = "/api/v1/playlist";
        Member member = createTestMember();

        MockMultipartFile imageFile = createTestImage("titleImage");

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
    public void updatePlaylistTest(TestReporter testReporter) throws Exception {
        // given
        Member member = createTestMember();
        Playlist playlist = createTestPlaylist(member);
        MockMultipartFile imageFile = createTestImage("titleImage");

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
        Member member = createTestMember();
        Playlist playlist = createTestPlaylist(member);

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
    public void deletePlaylistImageTest(TestReporter testReporter) throws Exception {
        // given
        Member member = createTestMember();
        Playlist playlist = createTestPlaylist(member);
        MockMultipartFile imageFile = createTestImage("titleImage");

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
    public void deletePlaylistImageWithPatchTest(TestReporter testReporter) throws Exception {
        // given
        Member member = createTestMember();
        Playlist playlist = createTestPlaylist(member);
        MockMultipartFile imageFile = createTestImage("titleImage");

        final String POST_API = "/api/v1/playlist";
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
}
