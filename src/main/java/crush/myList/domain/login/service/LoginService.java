package crush.myList.domain.login.service;

import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.global.enums.JwtTokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "LoginService")
@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * refresh_token 으로 access_token 재발급. x
    * */
    public Map<String, String> reissue(String token) {
        try {
            log.info("refresh token: {}", token);
            Jws<Claims> jws = jwtTokenProvider.validateAndParseToken(token);

            if (!jwtTokenProvider.isRefreshToken(jws)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "토큰 타입이 올바르지 않습니다.");
            }
            String memberId = jwtTokenProvider.getMemberId(jws);

            String accessToken = jwtTokenProvider.createToken(memberId, JwtTokenType.ACCESS_TOKEN);
            Map<String, String> json = new HashMap<>();
            json.put("access_token", accessToken);
            return json;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다.");
        }
    }
}
