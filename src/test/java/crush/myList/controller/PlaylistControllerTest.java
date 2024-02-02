package crush.myList.controller;

import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.entity.Role;
import crush.myList.domain.member.enums.RoleName;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.member.repository.RoleRepository;
import crush.myList.domain.music.Repository.MusicRepository;
import crush.myList.domain.music.entity.Music;
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
    private PlaylistRepository playlistRepository;
    @Autowired
    private MusicRepository musicRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("사용자의 모든 플레이리스트 조회 테스트")
    @Test
    public void viewAllPlaylistsTest(TestReporter testReporter) throws Exception {
        // given
        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다."));
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .role(role)
                .build();
        memberRepository.save(member);

        Playlist playlist = Playlist.builder()
                .name("testPlaylistName")
                .member(member)
                .build();
        playlistRepository.save(playlist);

        Music music = Music.builder()
                .title("굿굿")
                .artist("후후")
                .url("https://youtube.com")
                .playlist(playlist)
                .build();
        musicRepository.save(music);

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
        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다."));
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .role(role)
                .build();
        memberRepository.save(member);

        Playlist playlist = Playlist.builder()
                .name("testPlaylistName")
                .member(member)
                .build();
        playlistRepository.save(playlist);

        Music music = Music.builder()
                .title("굿굿")
                .artist("후후")
                .url("https://youtube.com")
                .playlist(playlist)
                .build();
        musicRepository.save(music);

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
    @Disabled
    public void createPlaylistTest(TestReporter testReporter) throws Exception {
        // given
        final String api = "/api/v1/playlist";
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .build();
        memberRepository.save(member);

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", //name
                "testImages.jpg", //originalFilename
                "image/jpeg",
                "testImageData".getBytes()
        );

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
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .build();
        memberRepository.save(member);

        Playlist playlist = Playlist.builder()
                .name("testPlaylistName")
                .member(member)
                .build();
        playlistRepository.save(playlist);

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", //name
                "testImages.jpg", //originalFilename
                "image/jpeg",
                "testImageData".getBytes()
        );

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
        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다."));
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .role(role)
                .build();
        memberRepository.save(member);

        Playlist playlist = Playlist.builder()
                .name("testPlaylistName")
                .member(member)
                .build();
        playlistRepository.save(playlist);

        final String api = "/api/v1/playlist/" + playlist.getId();

        testReporter.publishEntry(
                mockMvc.perform(
                        MockMvcRequestBuilders.delete(api)
                                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );
    }

    @DisplayName("플레이리스트 이미지 삭제 테스트")
    @Disabled
    @Test
    public void deletePlaylistImageTest(TestReporter testReporter) throws Exception {
        // given
        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다."));
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .role(role)
                .build();
        memberRepository.save(member);

        Playlist playlist = Playlist.builder()
                .name("testPlaylistName")
                .member(member)
                .build();
        playlistRepository.save(playlist);

        final String POST_API = "/api/v1/playlist";

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", //name
                "testImages.jpg", //originalFilename
                "image/jpeg",
                "testImageData".getBytes()
        );

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
        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다."));
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .role(role)
                .build();
        memberRepository.save(member);

        Playlist playlist = Playlist.builder()
                .name("testPlaylistName")
                .member(member)
                .build();
        playlistRepository.save(playlist);

        final String POST_API = "/api/v1/playlist";

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", //name
                "testImages.jpg", //originalFilename
                "image/jpeg",
                "testImageData".getBytes()
        );

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

        final String DELETE_IMAGE_API = "/api/v1/playlist/" + playlist.getId();

        // when
        testReporter.publishEntry(
                mockMvc.perform(
                                MockMvcRequestBuilders.patch(DELETE_IMAGE_API)
                                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .param("deletePlaylistImage", "true")
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.thumbnailUrl").isEmpty())
                        .andReturn().getResponse().getContentAsString()
        );
    }

    @DisplayName("플레이리스트 CRUD 통합 테스트")
    @Test
    @Disabled
    public void playlistCrudTest(TestReporter testReporter) throws Exception {
        // given
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .build();
        memberRepository.save(member);

        // TestImageFile
        MockMultipartFile imageFile = new MockMultipartFile(
                "titleImage", //name
                "testImages.jpg", //originalFilename
                "image/jpeg",
                "testImageData".getBytes()
        );

        final String accessToken = "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN);

        // when
        /* CREATE */
        final String POST_API = "/api/v1/playlist";
        testReporter.publishEntry(
                mockMvc.perform(
                        multipart(POST_API)
                                .file(imageFile)
                                .param("playlistName", "testPlaylist")
                                .header("Authorization", accessToken)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );

        /* READ */
        final String GET_API = "/api/v1/playlist/user/" + member.getUsername();
        testReporter.publishEntry(
                mockMvc.perform(get(GET_API))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );

        /* UPDATE */
        final String PATCH_API = "/api/v1/playlist/" + playlistRepository.findAllByMember(member).get(0).getId().toString();
        testReporter.publishEntry(
                mockMvc.perform(
                        multipart(HttpMethod.PATCH, PATCH_API)
                                .file(imageFile)
                                .param("playlistName", "testPlaylist")
                                .header("Authorization", accessToken)
                                .contentType(MediaType.MULTIPART_FORM_DATA))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );

        /* DELETE */
        final String DELETE_API = "/api/v1/playlist/" + playlistRepository.findAllByMember(member).get(0).getId().toString();
        testReporter.publishEntry(
                mockMvc.perform(
                        MockMvcRequestBuilders.delete(DELETE_API)
                                .header("Authorization", accessToken))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );
    }
}
