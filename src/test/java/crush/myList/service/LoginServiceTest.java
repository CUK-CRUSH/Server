package crush.myList.service;

import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.domain.login.service.LoginService;
import crush.myList.global.enums.JwtTokenType;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.*;
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
        String refresh = "refresh_token";
        String access = "access_token";
        String memberId = "1";
        Jws<Claims> jws = mock(Jws.class);

        given(jwtTokenProvider.validateAndParseToken(refresh)).willReturn(jws); // 토큰 검증
        given(jwtTokenProvider.isRefreshToken(jws)).willReturn(true); // 토큰 타입 검증
        given(jwtTokenProvider.getMemberId(jws)).willReturn(memberId); // 멤버 아이디 추출
        given(jwtTokenProvider.createToken(memberId, JwtTokenType.ACCESS_TOKEN)).willReturn(access); // 토큰 재발급

        // when
        Map<String, String> res = loginService.reissue(refresh);

        // then
        assertThat(res.get("access_token")).isEqualTo(access);
    }

    @Test
    @DisplayName("토큰 재발급 실패 테스트 - 만료된 토큰")
    public void reissueFailTest1() {
        // given
        String refresh = "expired_token";

        given(jwtTokenProvider.validateAndParseToken(refresh)).willThrow(new ExpiredJwtException(null, null, null)); // 토큰 검증

        // when
        Throwable throwable = catchThrowable(() -> loginService.reissue(refresh));

        // then
        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
        assertThat(((ResponseStatusException) throwable).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("토큰 재발급 실패 테스트 - 잘못된 토큰 타입")
    public void reissueFailTest2() {
        // given
        String refresh = "invalid_token";
        Jws<Claims> jws = mock(Jws.class);

        given(jwtTokenProvider.validateAndParseToken(refresh)).willReturn(jws); // 토큰 검증

        // when
        Throwable throwable = catchThrowable(() -> loginService.reissue(refresh));

        // then
        assertThat(throwable).isInstanceOf(ResponseStatusException.class);
    }

}
