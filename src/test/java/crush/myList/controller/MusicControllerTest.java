package crush.myList.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.music.dto.MusicDto;
import crush.myList.domain.music.mongo.document.Music;
import crush.myList.domain.music.mongo.repository.MusicRepository;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.global.enums.JwtTokenType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MusicControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TestUtil testUtil;
    @Autowired
    private MusicRepository musicRepository;

    @AfterEach
    void cleanUp() {
        musicRepository.deleteAll();
    }

    @DisplayName("음악 조회 테스트")
    @Test
    public void getMusicsTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);

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
    @Disabled
    public void postMusicTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);

        MusicDto.PostRequest postRequestDto = MusicDto.PostRequest.builder()
                .title("TestMusic")
                .artist("TestArtist")
                .url("https://youtube.com/watch?v=urx8-yfpY7c")
                .build();

        String request = objectMapper.writeValueAsString(postRequestDto);

        final String POST_API = "/api/v1/music/" + playlist.getId().toString();

        // when
        MockHttpServletResponse res = mockMvc.perform(
                MockMvcRequestBuilders.post(POST_API)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn().getResponse();
    }

    @Test
    @DisplayName("음악 여러곡 추가 테스트")
    public void postMultipleMusicTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);

        MusicDto.PostRequest postRequestDto1 = MusicDto.PostRequest.builder()
                .title("TestMusic")
                .artist("TestArtist")
                .url("https://youtube.com/watch?v=urx8-yfpY7c")
                .build();

        MusicDto.PostRequest postRequestDto2 = MusicDto.PostRequest.builder()
                .title("TestMusic2")
                .artist("TestArtist2")
                .url("https://youtube.com/watch?v=urx8-yfpY7c")
                .build();
        List<MusicDto.PostRequest> requestMusics = new ArrayList<MusicDto.PostRequest>();
        requestMusics.add(postRequestDto1);
        requestMusics.add(postRequestDto2);
        String request = objectMapper.writeValueAsString(requestMusics);

        final String POST_API = "/api/v1/music/" + playlist.getId().toString() + "/multiple";

        // when
        MockHttpServletResponse res = mockMvc.perform(
                MockMvcRequestBuilders.post(POST_API)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn().getResponse();
    }

    @Test
    @DisplayName("음악 수정 테스트")
//    @Disabled
    public void patchMusicTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        Music music = testUtil.createTestMusic(playlist);

        // when
        MusicDto.PatchRequest patchRequestDto = MusicDto.PatchRequest.builder()
                .title("updatedTitle")
                .artist("updatedArtist")
                .url("https://youtube.com/watch?v=urx8-yfpY7c")
                .build();

        String patchRequest = objectMapper.writeValueAsString(patchRequestDto);

        final String PATCH_API = "/api/v1/music?musicId=" + music.getId();

        testReporter.publishEntry(mockMvc.perform(
                MockMvcRequestBuilders.patch(PATCH_API)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.title").value("updatedTitle"))
                .andExpect(jsonPath("data.artist").value("updatedArtist"))
                .andExpect(jsonPath("data.url").value("https://youtube.com/watch?v=urx8-yfpY7c"))
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("음악 여러 곡 수정 테스트")
    public void patchMultipleMusicTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        Music music1 = testUtil.createTestMusic(playlist);
        Music music2 = testUtil.createTestMusic(playlist);

        // when
        MusicDto.PatchRequestV1 patchRequestDto1 = new MusicDto.PatchRequestV1();

        patchRequestDto1.setMusicId(music1.getId());
        patchRequestDto1.setMusicOrder(1);
        patchRequestDto1.setTitle("updatedTitle");
        patchRequestDto1.setArtist("updatedArtist");
        patchRequestDto1.setUrl("https://youtube.com/watch?v=urx8-yfpY7c");

        MusicDto.PatchRequestV1 patchRequestDto2 = new MusicDto.PatchRequestV1();

        patchRequestDto2.setMusicId(music2.getId());
        patchRequestDto2.setMusicOrder(2);
        patchRequestDto2.setTitle("updatedTitle2");
        patchRequestDto2.setArtist("updatedArtist2");
        patchRequestDto2.setUrl("https://youtube.com/watch?v=urx8-yfpY7c");

        List<MusicDto.PatchRequestV1> patchRequests = new ArrayList<>();
        patchRequests.add(patchRequestDto1);
        patchRequests.add(patchRequestDto2);
        String patchRequest = objectMapper.writeValueAsString(patchRequests);

        System.out.println(patchRequest);

        final String PATCH_API = "/api/v1/music/" + playlist.getId() + "/multiple";

        testReporter.publishEntry(mockMvc.perform(
                MockMvcRequestBuilders.patch(PATCH_API)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data[0].title").value("updatedTitle"))
                .andExpect(jsonPath("data[0].artist").value("updatedArtist"))
                .andExpect(jsonPath("data[0].url").value("https://youtube.com/watch?v=urx8-yfpY7c"))
                .andExpect(jsonPath("data[1].title").value("updatedTitle2"))
                .andExpect(jsonPath("data[1].artist").value("updatedArtist2"))
                .andExpect(jsonPath("data[1].url").value("https://youtube.com/watch?v=urx8-yfpY7c"))
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("음악 수정 실패 테스트 - 잘못된 URL")
    public void patchMusicFailTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("testUser");
        Playlist playlist = testUtil.createTestPlaylist(member);
        Music music = testUtil.createTestMusic(playlist);

        MusicDto.PatchRequest patchRequestDto = MusicDto.PatchRequest.builder()
                .title("TestMusic2")
                .artist("TestArtist2")
                .url("https://youtube.com/watch?v=urx8-yfpY72c")  // 잘못된 URL
                .build();

        String patchRequest = objectMapper.writeValueAsString(patchRequestDto);

        final String PATCH_API = "/api/v1/music?musicId=" + music.getId();

        testReporter.publishEntry(mockMvc.perform(
                MockMvcRequestBuilders.patch(PATCH_API)
                        .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(patchRequest))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().toString());
    }
}
