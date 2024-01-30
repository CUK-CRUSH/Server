package crush.myList.service;

import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.domain.login.service.LoginService;
import crush.myList.global.enums.JwtTokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@Transactional
@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 테스트")
public class LoginServiceTest {
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @InjectMocks
    private LoginService loginService;

    @Test
    @DisplayName("토큰 재발급 성공 테스트")
    public void reissueSuccessTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class); // 로그인 요청 객체
        String refresh = "refresh_token";
        String access = "access_token";
        String memberId = "1";
        Jws<Claims> jws = mock(Jws.class);

        given(jwtTokenProvider.resolveToken(request)).willReturn(refresh); // 토큰 추출
        given(jwtTokenProvider.validateAndParseToken(refresh)).willReturn(jws); // 토큰 검증
        given(jwtTokenProvider.isRefreshToken(jws)).willReturn(true); // 토큰 타입 검증
        given(jwtTokenProvider.getMemberId(jws)).willReturn(memberId); // 멤버 아이디 추출
        given(jwtTokenProvider.createToken(memberId, JwtTokenType.ACCESS_TOKEN)).willReturn(access); // 토큰 재발급

        // when
        Map<String, String> res = loginService.reissue(request);

        // then
        assertThat(res.get("access_token")).isEqualTo(access);
    }
}
