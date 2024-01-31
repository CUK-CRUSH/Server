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
public class MusicControllerTest extends TestHelper {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("음악 조회 테스트")
    @Test
    public void getMusicsTest(TestReporter testReporter) throws Exception {
        // given
        Member member = createTestMember();
        Playlist playlist = createTestPlaylist(member);

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
        Playlist playlist = createTestPlaylist(member);

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
        Playlist playlist = createTestPlaylist(member);

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
        Playlist playlist = createTestPlaylist(member);

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
