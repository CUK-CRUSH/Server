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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
public class MemberControllerTest {
    private MockMvc mvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @BeforeEach
    void beforeAll(WebApplicationContext webApplicationContext) {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .defaultRequest(post("/**").with(csrf()))
                .build();
    }

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
        testReporter.publishEntry(mvc.perform(get("/api/v1/member/nickname/check/{username}", "test2")
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
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
        testReporter.publishEntry(mvc.perform(post("/api/v1/member/nickname/change/{username}", "test2")
                .header("Authorization", "Bearer " + jwtTokenProvider.createToken(member.getId().toString(), JwtTokenType.ACCESS_TOKEN)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
    }
}
