package crush.myList.config.jwt;

import crush.myList.config.security.SecurityMemberService;
import crush.myList.domain.member.entity.Member;
import crush.myList.domain.member.repository.MemberRepository;
import crush.myList.global.enums.JwtTokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.Date;

// 토큰을 생성하고 검증하는 클래스입니다.
// 해당 컴포넌트는 필터클래스에서 사전 검증을 거칩니다.
@Component
@Transactional
@Slf4j(topic = "JwtTokenProvider")
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-time}")
    private long accessTokenTime;
    @Value("${jwt.refresh-token-time}")
    private long refreshTokenTime;

    private final MemberRepository memberRepository;
    private final SecurityMemberService SecurityMemberService;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public String createToken(String memberId, JwtTokenType tokenType) throws IllegalArgumentException {
        // 멤버 조회
        Member member = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        // 토큰 생성
        Claims claims = Jwts.claims().setSubject(member.getId().toString()); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.
        claims.put("type", tokenType.getTokenType()); // 토큰 타입 (access token, refresh token)
        Date now = new Date();
        Date expiration;

        if (tokenType == JwtTokenType.REFRESH_TOKEN) { // refresh token
            expiration = new Date(now.getTime() + refreshTokenTime);
        } else if (tokenType == JwtTokenType.ACCESS_TOKEN) { // access token
            expiration = new Date(now.getTime() + accessTokenTime);
        } else {
            throw new IllegalArgumentException("토큰 타입이 올바르지 않습니다.");
        }

        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(expiration) // set Expire Time
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256) // signature 에 들어갈 secret값 세팅
                .compact();
    }
//    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(Jws<Claims> claimsJws) throws IllegalArgumentException {
        String memberId = claimsJws.getBody().getSubject();
        Member member = memberRepository.findById(Long.parseLong(memberId))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        UserDetails userDetails = SecurityMemberService.loadUserByMemberId(member.getId().toString());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

// JWT 토큰에서 회원 정보 추출
    public String getMemberId(Jws<Claims> jws) {
        return jws.getBody().getSubject();
    }

//    // Request의 Header에서 token 값을 가져옵니다. "Authorization" : Bearer [TOKEN 값]'
    public String resolveToken(HttpServletRequest request) {
        String value = request.getHeader("Authorization");
        if (value != null && value.startsWith("Bearer ")) {
            return value.substring(7);
        } else {
            return null;
        }
    }

//    // 토큰의 유효성 + 만료일자 확인
    public Jws<Claims> validateAndParseToken(String jwt) {
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build();
        return jwtParser.parseClaimsJws(jwt);
    }

    public boolean isRefreshToken(Jws<Claims> jws) {
        return jws.getBody().get("type").equals(JwtTokenType.REFRESH_TOKEN.getTokenType());
    }

    public boolean isAccessToken(Jws<Claims> jws) {
        return jws.getBody().get("type").equals(JwtTokenType.ACCESS_TOKEN.getTokenType());
    }
}
