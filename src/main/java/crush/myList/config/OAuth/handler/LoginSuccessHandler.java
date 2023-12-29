package crush.myList.config.OAuth.handler;

import crush.myList.config.OAuth.users.GoogleUser;
import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // OAuth2User로 캐스팅하여 인증된 사용자 정보를 가져온다.
        GoogleUser oAuth2User = (GoogleUser) authentication.getPrincipal();
        // 토큰 생성
        String token = jwtTokenProvider.createToken(oAuth2User.getMemberId());
        // 토큰을 query parameter로 붙여서 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, "/login/oauth2/success?token=" + token);
        log.info("로그인 성공");
    }
}
