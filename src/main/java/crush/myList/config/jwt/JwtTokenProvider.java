package crush.myList.config.jwt;

import crush.myList.config.security.SecurityMemberDto;
import crush.myList.member.entity.Member;
import crush.myList.member.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

// 토큰을 생성하고 검증하는 클래스입니다.
// 해당 컴포넌트는 필터클래스에서 사전 검증을 거칩니다.
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-time}")
    private long accessTokenTime;

    private final MemberRepository memberRepository;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public String createToken(String memberId) throws IllegalArgumentException {
        Claims claims = Jwts.claims().setSubject(memberId); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + accessTokenTime)) // set Expire Time
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256) // signature 에 들어갈 secret값 세팅
                .compact();
    }
//    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        Member member = memberRepository.findById(Long.parseLong(getMemberId(token))).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        SecurityMemberDto userDetails = SecurityMemberDto.builder()
                .id(String.valueOf(member.getId()))
                .username(member.getUsername())
                .oauth2id(member.getOauth2id())
                .name(member.getName())
                .build();
        // todo: 권한 부여 설정해야함
        return new UsernamePasswordAuthenticationToken(userDetails, "", null);
    }

//    // 토큰에서 회원 정보 추출
    public String getMemberId(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes()).build();

        return jwtParser.parseClaimsJws(token).getBody().getSubject();
    }
//
//    // Request의 Header에서 token 값을 가져옵니다. "Authorization" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

//    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String token) {
        if (token == null || !token.contains("Bearer ")) {
            return false;
        }
        String jwt = token.replace("Bearer ", "");
        try {
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build();
            Jws<Claims> claims = jwtParser.parseClaimsJws(jwt);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
