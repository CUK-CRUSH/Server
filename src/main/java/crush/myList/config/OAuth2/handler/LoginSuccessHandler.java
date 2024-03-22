package crush.myList.config.OAuth2.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import crush.myList.config.EnvBean;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final EnvBean envBean;

    public String createURI(String accessToken, String refreshToken) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", accessToken);
        queryParams.add("refresh_token", refreshToken);

        return UriComponentsBuilder
                .newInstance()
                .uri(URI.create(envBean.getReactUri() + "/redirect"))
                .queryParams(queryParams)
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // OAuth2User로 캐스팅하여 인증된 사용자 정보를 가져온다.
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        // 토큰 생성
        String accessToken = jwtTokenProvider.createToken(oAuth2User.getMemberId(), JwtTokenType.ACCESS_TOKEN);
        String refreshToken = jwtTokenProvider.createToken(oAuth2User.getMemberId(), JwtTokenType.REFRESH_TOKEN);

        String uri = createURI(accessToken, refreshToken);
        response.sendRedirect(uri);
    }
}
