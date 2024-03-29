package crush.myList.controller;

import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.domain.image.entity.Image;
import crush.myList.domain.image.repository.ImageRepository;
import crush.myList.domain.image.service.ImageService;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.member.repository.RoleRepository;
import crush.myList.domain.music.mongo.document.Music;
import crush.myList.domain.music.mongo.repository.MusicRepository;
import crush.myList.domain.playlist.entity.Playlist;
import crush.myList.domain.playlist.entity.PlaylistLike;
import crush.myList.domain.playlist.repository.PlaylistRepository;
import crush.myList.global.enums.JwtTokenType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.net.URL;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class MemberControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private TestUtil testUtil;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private PlaylistRepository playlistRepository;
    @Autowired
    private MusicRepository musicRepository;

    @AfterEach
    void cleanUp() {
        musicRepository.deleteAll();
    }

    @Test
    @DisplayName("닉네임 중복 확인 테스트")
    void checkUsernameTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("TestMember");
        memberRepository.save(member);
        // when
        testReporter.publishEntry(mvc.perform(get("/api/v1/member/nickname/available/{username}", "test2")
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("닉네임 중복 확인 실패 테스트 - 중복된 닉네임")
    void checkUsernameFailTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("test");
        memberRepository.save(member);
        // when
        testReporter.publishEntry(mvc.perform(get("/api/v1/member/nickname/available/{username}", "test")
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().toString());
    }

    @Test
    @DisplayName("닉네임 변경 테스트")
    void changeUsernameTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("TestMember");
        memberRepository.save(member);

        // when
        testReporter.publishEntry(mvc.perform(patch("/api/v1/member/me")
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("username", "test2"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("닉네임 변경 실패 테스트 - 중복된 닉네임")
    void changeUsernameFailTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("test");
        memberRepository.save(member);

        // when
        testReporter.publishEntry(mvc.perform(patch("/api/v1/member/me")
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("username", "test"))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().toString());
    }

    @Test
    @DisplayName("내 정보 조회 테스트")
    void viewMyInfoTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("TestMember");
        memberRepository.save(member);
        // when
        testReporter.publishEntry(mvc.perform(get("/api/v1/member/me")
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("내 정보 조회 실패 테스트 - 존재하지 않는 사용자")
    void viewMyInfoFailTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("TestMember");
        memberRepository.save(member);
        String token = jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN);
        memberRepository.deleteById(member.getId());
        // when
        testReporter.publishEntry(mvc.perform(get("/api/v1/member/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("회원 정보 조회 테스트 - id로 조회")
    void viewMemberInfoTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("TestMember");
        memberRepository.save(member);
        // when
        testReporter.publishEntry(mvc.perform(get("/api/v1/member/id/{id}", member.getId())
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(member.getUsername()))
                .andExpect(jsonPath("$.data.view.totalViews").value(1))
                .andExpect(jsonPath("$.data.view.todayViews").value(1))
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("회원 정보 조회 테스트 - 닉네임으로 조회")
    void viewMemberInfoTest2(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("TestMember");
        memberRepository.save(member);
        // when
        testReporter.publishEntry(mvc.perform(get("/api/v1/member/nickname/{username}", member.getUsername())
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(member.getUsername()))
                .andExpect(jsonPath("$.data.view.totalViews").value(1))
                .andExpect(jsonPath("$.data.view.todayViews").value(1))
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("회원 정보 조회 실패 테스트 - 존재하지 않는 사용자")
    void viewMemberInfoFailTest(TestReporter testReporter) throws Exception {
        // given
        // when
        testReporter.publishEntry(mvc.perform(get("/api/v1/member/id/{id}", "999999999"))
//                .header("Authorization", "Bearer " + jwtTokenProvider.createToken("1", JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().toString());
    }

    @Test
    @Disabled // 매번 테스트마다 GCS에 저장되는 것을 방지하기 위해 비활성화
    @DisplayName("내 정보 수정 테스트")
    void updateMyInfoTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("TestMember");
        memberRepository.save(member);

        URL resource = getClass().getResource("/img/test.png");
        FileInputStream file1 = new FileInputStream(resource.getFile());
        FileInputStream file2 = new FileInputStream(resource.getFile());
        MockMultipartFile profileImageFile = new MockMultipartFile("profileImage", "image.jpg", "image/jpeg", file1);
        MockMultipartFile backgroundImageFile = new MockMultipartFile("backgroundImage", "image.jpg", "image/jpeg", file2);

        // when
        testReporter.publishEntry(mvc.perform(multipart(HttpMethod.PATCH, "/api/v1/member/me").file(profileImageFile).file(backgroundImageFile)
                .param("username","test2")
                .param("introduction","test2")
                .param("deleteProfileImage", "true")
                .param("deleteBackgroundImage", "true")
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("내 정보 수정 실패 테스트 - 존재하지 않는 사용자")
    void updateMyInfoFailTest(TestReporter testReporter) throws Exception {
        // given
        MockMultipartFile profileImageFile = new MockMultipartFile("profileImage", "image.jpg", "image/jpeg", "image".getBytes());
        MockMultipartFile backgroundImageFile = new MockMultipartFile("backgroundImage", "image.jpg", "image/jpeg", "image".getBytes());
        Member member = testUtil.createTestMember("TestMember");
        memberRepository.save(member);
        String token = jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN);
        memberRepository.deleteById(member.getId());

        // when
        testReporter.publishEntry(mvc.perform(multipart(HttpMethod.PATCH, "/api/v1/member/me").file(profileImageFile).file(backgroundImageFile)
                .param("username","test2")
                .param("introduction","test2")
                .header("Authorization", "Bearer " + token)
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void deleteMemberTest(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("TestMember");

        // when
        testReporter.publishEntry(mvc.perform(delete("/api/v1/member/me")
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        // then
        Assertions.assertTrue(memberRepository.findById(member.getId()).isEmpty());
    }

    @Test
    @DisplayName("회원 탈퇴 테스트 - 이미지 및 플레이리스트 / 좋아요 삭제")
    void deleteMemberTest2(TestReporter testReporter) throws Exception {
        // given
        Member member = testUtil.createTestMember("TestMember");
        Image profileImage = imageService.saveImageToGcs_Image(testUtil.createTestImage("profileImage"));
        Image backgroundImage = imageService.saveImageToGcs_Image(testUtil.createTestImage("backgroundImage"));
        member.setProfileImage(profileImage);
        member.setBackgroundImage(backgroundImage);
        memberRepository.save(member);

        Playlist playlist = testUtil.createTestPlaylist(member);
        Image playlistImage = imageService.saveImageToGcs_Image(testUtil.createTestImage("playlistImage"));
        playlist.setImage(playlistImage);
        playlistRepository.save(playlist);

        PlaylistLike playlistLike = testUtil.createTestPlaylistLike(member, playlist);

        Music music = testUtil.createTestMusic(playlist);
        musicRepository.save(music);

        // when
        testReporter.publishEntry(mvc.perform(delete("/api/v1/member/me")
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                .with(csrf()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());

        // then
        Assertions.assertTrue(memberRepository.findById(member.getId()).isEmpty());
        Assertions.assertTrue(imageRepository.findById(profileImage.getId()).isEmpty());
        Assertions.assertTrue(imageRepository.findById(backgroundImage.getId()).isEmpty());
        Assertions.assertTrue(playlistRepository.findById(playlist.getId()).isEmpty());
        Assertions.assertTrue(imageRepository.findById(playlistImage.getId()).isEmpty());
        Assertions.assertTrue(musicRepository.findById(music.getId()).isEmpty());
    }

    @DisplayName("내가 좋아요한 플레이리스트 조회 테스트")
    @Test
    public void viewMyLikedPlaylistsTest(TestReporter testReporter) throws Exception {
        // given
        Member member1 = testUtil.createTestMember("testUser");
        Member member2 = testUtil.createTestMember("otherUser");

        for (int i = 0; i < 10; i++) {
            Playlist playlist = testUtil.createTestPlaylist(member2);
            PlaylistLike playlistLike = testUtil.createTestPlaylistLike(member1, playlist);
        }

        final String api = "/api/v1/member/playlist/like";

        // when
        testReporter.publishEntry(
                mvc.perform(get(api)
                                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member1.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                                .param("page", "0")
                                .param("username", member1.getUsername()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.length()").value(8))
                        .andReturn().getResponse().getContentAsString()
        );

        testReporter.publishEntry(
                mvc.perform(get(api)
                                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member1.getId().toString(), JwtTokenType.ACCESS_TOKEN))
                                .param("page", "1")
                                .param("username", member1.getUsername()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.data.length()").value(2))
                        .andReturn().getResponse().getContentAsString()
        );
    }
}
