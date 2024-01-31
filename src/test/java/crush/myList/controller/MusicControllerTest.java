package crush.myList.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.entity.Role;
import crush.myList.domain.member.enums.RoleName;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.member.repository.RoleRepository;
import crush.myList.domain.music.Dto.MusicDto;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MusicControllerTest {
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
    @Autowired
    private ObjectMapper objectMapper;

    public Member createTestMember() {
        Role role = roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다."));
        return Member.builder()
                .oauth2id("test:1")
                .username("test")
                .name("test1")
                .role(role)
                .build();
    }

    @DisplayName("음악 CRUD 통합테스트")
    @Test
    @Disabled
    public void musicCrudTest(TestReporter testReporter) throws Exception {
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
                .member(member)
                .name("TestPlaylist")
                .build();
        playlistRepository.save(playlist);

        MusicDto.PostRequest postRequestDto = MusicDto.PostRequest.builder()
                .title("TestMusic")
                .artist("TestArtist")
                .url("https://youtube.com")
                .build();

        String request = objectMapper.writeValueAsString(postRequestDto);

        final String accessToken = "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN);

        // when
        /* CREATE */
        final String POST_API = "/api/v1/music/" + playlist.getId().toString();

        testReporter.publishEntry(
                mockMvc.perform(
                        MockMvcRequestBuilders.post(POST_API)
                                .header("Authorization", accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );

        /* READ */
        final String GET_API = "/api/v1/music/" + playlist.getId().toString();

        testReporter.publishEntry(
                mockMvc.perform(
                        MockMvcRequestBuilders.get(GET_API)
                )
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );

        /* DELETE */
        final String DELETE_API = "/api/v1/music";

        testReporter.publishEntry(
                mockMvc.perform(
                        MockMvcRequestBuilders.delete(DELETE_API)
                                .header("Authorization", accessToken)
                                .param("musicId", musicRepository.findAll().get(0).getId().toString()))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()
        );
    }

    @DisplayName("음악 조회 테스트")
    @Test
    public void getMusicsTest(TestReporter testReporter) throws Exception {
        // given
        Member member = createTestMember();
        memberRepository.save(member);

        Playlist playlist = Playlist.builder()
                .member(member)
                .name("TestPlaylist")
                .build();
        playlistRepository.save(playlist);

        final String GET_API = "/api/v1/music/" + playlist.getId().toString() + "?page=0";

        // when
        testReporter.publishEntry(mockMvc.perform(
                MockMvcRequestBuilders.get(GET_API)
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse().getContentAsString());
    }

    @Test
    @DisplayName("음악 추가 테스트")
    public void postMusicTest(TestReporter testReporter) throws Exception {
        // given
        Member member = createTestMember();
        memberRepository.save(member);

        Playlist playlist = Playlist.builder()
                .member(member)
                .name("TestPlaylist")
                .build();
        playlistRepository.save(playlist);

        MusicDto.PostRequest postRequestDto = MusicDto.PostRequest.builder()
                .title("TestMusic")
                .artist("TestArtist")
                .url("https://youtube.com/watch?v=urx8-yfpY7c")
                .build();

        String request = objectMapper.writeValueAsString(postRequestDto);

        final String POST_API = "/api/v1/music/" + playlist.getId().toString();

        // when
        testReporter.publishEntry(mockMvc.perform(
                MockMvcRequestBuilders.post(POST_API)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("음악 수정 테스트")
    public void patchMusicTest(TestReporter testReporter) throws Exception {
        // given
        Member member = createTestMember();
        memberRepository.save(member);

        Playlist playlist = Playlist.builder()
                .member(member)
                .name("TestPlaylist")
                .build();
        playlistRepository.save(playlist);

        MusicDto.PostRequest postRequestDto = MusicDto.PostRequest.builder()
                .title("TestMusic")
                .artist("TestArtist")
                .url("https://youtube.com/watch?v=urx8-yfpY7c")
                .build();

        String request = objectMapper.writeValueAsString(postRequestDto);

        final String POST_API = "/api/v1/music/" + playlist.getId().toString();

        // when
        testReporter.publishEntry(mockMvc.perform(
                MockMvcRequestBuilders.post(POST_API)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        MusicDto.PatchRequest patchRequestDto = MusicDto.PatchRequest.builder()
                .title("TestMusic2")
                .artist("TestArtist2")
                .url("https://youtube.com/watch?v=urx8-yfpY7c")
                .build();

        String patchRequest = objectMapper.writeValueAsString(patchRequestDto);

        final String PATCH_API = "/api/v1/music?musicId=" + musicRepository.findAll().get(0).getId().toString();

        testReporter.publishEntry(mockMvc.perform(
                MockMvcRequestBuilders.patch(PATCH_API)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchRequest))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("음악 수정 실패 테스트 - 잘못된 URL")
    public void patchMusicFailTest(TestReporter testReporter) throws Exception {
        // given
        Member member = createTestMember();
        memberRepository.save(member);

        Playlist playlist = Playlist.builder()
                .member(member)
                .name("TestPlaylist")
                .build();
        playlistRepository.save(playlist);

        MusicDto.PostRequest postRequestDto = MusicDto.PostRequest.builder()
                .title("TestMusic")
                .artist("TestArtist")
                .url("https://youtu.be/pWRcCeKdd6Y?si=tBID5-iK-qAEhHEJ")
                .build();

        String request = objectMapper.writeValueAsString(postRequestDto);

        final String POST_API = "/api/v1/music/" + playlist.getId().toString();

        // when
        testReporter.publishEntry(mockMvc.perform(
                MockMvcRequestBuilders.post(POST_API)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        MusicDto.PatchRequest patchRequestDto = MusicDto.PatchRequest.builder()
                .title("TestMusic2")
                .artist("TestArtist2")
                .url("https://youtube.com/watch?v=urx8-yfpY72c")  // 잘못된 URL
                .build();

        String patchRequest = objectMapper.writeValueAsString(patchRequestDto);

        final String PATCH_API = "/api/v1/music?musicId=" + musicRepository.findAll().get(0).getId().toString();

        testReporter.publishEntry(mockMvc.perform(
                MockMvcRequestBuilders.patch(PATCH_API)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchRequest))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().toString());
    }
}
