package crush.myList.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//해당 클래스는 JwtTokenProvider가 검증을 끝낸 Jwt로부터 유저 정보를 조회해와서 UserPasswordAuthenticationFilter 로 전달합니다.
@Slf4j(topic = "JwtAuthenticationFilter")
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException {
        try {
            // 헤더에서 JWT 를 받아옵니다.
            String jwt = jwtTokenProvider.resolveToken(request);
            // 토큰이 존재하는 경우
            if (jwt != null) {
                // 유효한 토큰인지 확인합니다.
                Jws<Claims> claims = jwtTokenProvider.validateAndParseToken(jwt);
                // 토큰 타입이 올바른지 확인합니다.
                if (!jwtTokenProvider.isAccessToken(claims)) {
                    throw new JwtException("토큰 타입이 올바르지 않습니다.");
                }
                log.info("토큰 검증 완료");
                // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
                Authentication authentication = jwtTokenProvider.getAuthentication(claims);
                log.info("authentication: {}", authentication);
                // SecurityContext 에 Authentication 객체를 저장합니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("SecurityContext 에 Authentication 객체 저장 완료");
            }
            // 요청으로 들어온 request, response 를 다음 필터로 넘깁니다.
            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) { // 유효하지 않은 토큰
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Expired token");
        } catch (JwtException e) { // 유효하지 않은 토큰
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            log.error(e.getMessage());
        } catch (Exception e) { // 올바르지 않은 토큰
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Invalid token");
            log.error(e.getMessage());
        }
    }
}