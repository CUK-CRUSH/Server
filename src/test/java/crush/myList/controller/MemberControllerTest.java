package crush.myList.controller;

import crush.myList.config.EnvBean;
import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.config.security.SecurityUserDetailsService;
import crush.myList.domain.member.controller.MemberController;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.domain.member.service.MemberService;
import crush.myList.global.enums.JwtTokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class MemberControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("닉네임 중복 확인 테스트")
    void checkUsernameTest(TestReporter testReporter) throws Exception {
        // given
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .build();
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
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .build();
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
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .build();
        memberRepository.save(member);
        // when
        testReporter.publishEntry(mvc.perform(put("/api/v1/member/nickname/{username}", "test2")
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("닉네임 변경 실패 테스트 - 중복된 닉네임")
    void changeUsernameFailTest(TestReporter testReporter) throws Exception {
        // given
        Member member = Member.builder()
                .oauth2id("1")
                .username("test2")
                .name("test1")
                .build();
        memberRepository.save(member);
        // when
        testReporter.publishEntry(mvc.perform(put("/api/v1/member/nickname/{username}", "test2")
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().toString());
    }

    @Test
    @DisplayName("내 정보 조회 테스트")
    void viewMyInfoTest(TestReporter testReporter) throws Exception {
        // given
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .build();
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
        String token = jwtTokenProvider.createToken("1", JwtTokenType.ACCESS_TOKEN);
        memberRepository.deleteById(1L);
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
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .build();
        memberRepository.save(member);
        // when
        testReporter.publishEntry(mvc.perform(get("/api/v1/member/id/{id}", member.getId())
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("회원 정보 조회 테스트 - 닉네임으로 조회")
    void viewMemberInfoTest2(TestReporter testReporter) throws Exception {
        // given
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .build();
        memberRepository.save(member);
        // when
        testReporter.publishEntry(mvc.perform(get("/api/v1/member/nickname/{username}", member.getUsername())
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
    }

    @Test
    @DisplayName("회원 정보 조회 실패 테스트 - 존재하지 않는 사용자")
    void viewMemberInfoFailTest(TestReporter testReporter) throws Exception {
        // given
        // when
        testReporter.publishEntry(mvc.perform(get("/api/v1/member/{id}", "1")
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken("1", JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().toString());
    }

    @Test
    @Disabled // 매번 테스트마다 GCS에 저장되는 것을 방지하기 위해 비활성화
    @DisplayName("내 정보 수정 테스트")
    void updateMyInfoTest(TestReporter testReporter) throws Exception {
        // given
        Member member = Member.builder()
                .oauth2id("1")
                .username("test")
                .name("test1")
                .build();
        memberRepository.save(member);

        MockMultipartFile profileImageFile = new MockMultipartFile("profileImage", "image.jpg", "image/jpeg", "image".getBytes());
        MockMultipartFile backgroundImageFile = new MockMultipartFile("backgroundImage", "image.jpg", "image/jpeg", "image".getBytes());

        // when
        testReporter.publishEntry(mvc.perform(multipart(HttpMethod.PATCH, "/api/v1/member").file(profileImageFile).file(backgroundImageFile)
                .param("username","test2")
                .param("introduction","test2")
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
        String token = jwtTokenProvider.createToken("1", JwtTokenType.ACCESS_TOKEN);
        memberRepository.deleteById(1L);

        // when
        testReporter.publishEntry(mvc.perform(multipart(HttpMethod.PATCH, "/api/v1/member").file(profileImageFile).file(backgroundImageFile)
                .param("username","test2")
                .param("introduction","test2")
                .header("Authorization", "Bearer " + token)
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString());
    }
}
