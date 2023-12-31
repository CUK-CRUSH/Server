package crush.myList.config.OAuth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import crush.myList.config.OAuth2.users.CustomOAuth2User;
import crush.myList.config.OAuth2.users.GoogleUser;
import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.global.dto.JsonBody;
import crush.myList.global.dto.ResponseBody;
import crush.myList.global.enums.JwtTokenType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // OAuth2User로 캐스팅하여 인증된 사용자 정보를 가져온다.
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        // 토큰 생성
        String accessToken = jwtTokenProvider.createToken(oAuth2User.getMemberId(), JwtTokenType.ACCESS_TOKEN);
        String refreshToken = jwtTokenProvider.createToken(oAuth2User.getMemberId(), JwtTokenType.REFRESH_TOKEN);

        Map<String, String> token = new HashMap<>();
        token.put("access_token", accessToken);
        token.put("refresh_token", refreshToken);

        JsonBody<Object> jsonBody = JsonBody.of(HttpStatus.OK.value(), "로그인 성공", token);

        // content-type을 json, 인코딩을 utf-8로 설정
        response.setContentType("application/json; charset=utf-8");

        // 객체를 JSON으로 변환
        ObjectMapper mapper = new ObjectMapper();
        // JSON 형태로 변환된 객체를 Response에 담아준다.
        mapper.writeValue(response.getWriter(), jsonBody);
        log.info("로그인 성공");
    }
}
