package crush.myList.domain.login.service;

import crush.myList.config.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
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

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginService {
    private final JwtTokenProvider jwtTokenProvider;

    public Map<String, String> reissue(HttpServletRequest request) {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            Jws<Claims> jws = jwtTokenProvider.validateAndParseToken(token);
            String memberId = jwtTokenProvider.getMemberId(jws);

            String accessToken = jwtTokenProvider.createToken(memberId, JwtTokenProvider.ACCESS_TOKEN);
            Map<String, String> json = new HashMap<>();
            json.put("access_token", accessToken);
            return json;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "토큰이 유효하지 않습니다.");
        }
    }
}
