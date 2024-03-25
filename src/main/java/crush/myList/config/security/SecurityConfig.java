package crush.myList.config.security;

import crush.myList.config.EnvBean;
import crush.myList.config.OAuth2.handler.LoginFailureHandler;
import crush.myList.config.OAuth2.handler.LoginSuccessHandler;
import crush.myList.config.OAuth2.OAuth2Service;
import crush.myList.config.jwt.JwtAuthenticationFilter;
import crush.myList.config.jwt.JwtTokenProvider;
import crush.myList.domain.member.enums.RoleName;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import static org.springframework.security.config.Customizer.withDefaults;

// spring security 설정 파일 입니다.
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity() // prePostEnabled 어노테이션 활성화
public class SecurityConfig {
    private final OAuth2Service OAuth2Service;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final EnvBean envBean;

    private final String[] GET_LIST = {
        // 플레이리스트 조회 api
        "/api/v1/playlist/user/{username}",
        "/api/v1/playlist/{playlistId}",
        // 플레이리스트 좋아요 조회 api
        "/api/v1/playlist/{playlistId}/like",
        // 플레이리스트 방명록 조회 api
        "/api/v1/playlist/{playlistId}/guestbook",
        // 음악 조회 api
        "/api/v1/music/{playlistId}",
        // 사용자 조회 api
        "/api/v1/member/id/{id}",
        "/api/v1/member/nickname/{username}",
        "/api/v1/member/nickname/available/{username}",
        "/api/v1/member/playlist/like",
        // 자동완성 api
        "/api/v1/autocomplete/**",

        // 플레이리스트 추천 api
        "/api/v1/recommendation",

        // 검색 api
        "/api/v1/search",
        "/api/v1/search/playlist",
        "/api/v1/search/member",

        // 랭킹 api
        "/api/v1/ranking/**",
    };

    private final String[] TEMPORARY_LIST = {
        // 사용자 api
        "/api/v1/member/me",
        "/api/v1/member/me/{username}",
    };

    private final String[] WHITE_LIST = {
        // swagger
        "/v3/api-docs/**",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/swagger-resources/**",

        // redirect
        "/login/oauth2/success",
        "/login/oauth2/callback",
        "/login/oauth2/code/**",

        // refresh token으로 access token 재발급
        "/login/token/reissue",

        // 축제 매칭 신청 api
        "/api/v1/festival/match",

        // 에러 페이지
        "/error"
    };

    @Bean
    protected SecurityFilterChain apiConfig(HttpSecurity http) throws Exception {
        // 로그인 설정
        http.oauth2Login(oauth2 -> oauth2
                    .userInfoEndpoint(userInfo -> userInfo
                            .userService(OAuth2Service)) // 유저 정보 가져오기
                    .loginPage(envBean.getReactUri() + "/login") // 로그인 페이지
                    .successHandler(loginSuccessHandler) // 로그인 성공
                    .failureHandler(loginFailureHandler) // 로그인 실패
                    .permitAll())

            .formLogin(form -> form
                    .loginPage("/admin/login") // 로그인 페이지
                    .loginProcessingUrl("/admin/login") // 로그인 처리 url
                    .defaultSuccessUrl("/admin/home", true) // 로그인 성공시 이동할 페이지
                    .failureUrl("/admin/login?error=true") // 로그인 실패시 이동할 페이지
                    .permitAll()) // 로그인 페이지는 모든 사용자 허용
            .logout(logout -> logout
                    .logoutUrl("/admin/logout") // 로그아웃 url
                    .deleteCookies("JSESSIONID") // 쿠키 삭제
                    .logoutSuccessUrl("/admin/login") // 로그아웃 성공시 이동할 페이지
                    .permitAll()) // 로그아웃 페이지는 모든 사용자 허용

            // 허용 경로 설정
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(WHITE_LIST).permitAll()  // 모든 사용자 허용 경로 (모든 메소드)
                    .requestMatchers(HttpMethod.GET, GET_LIST).permitAll()  // 모든 사용자 허용 경로 (GET 메소드)
                    .requestMatchers(TEMPORARY_LIST).hasAnyRole(RoleName.TEMPORARY.name(), RoleName.USER.name())  // 임시 유저 이상만 허용
                    .requestMatchers("/api/v1/**").hasAnyRole(RoleName.USER.name())  // 닉네임이 설정된 USER 권한 이상만 허용
                    .requestMatchers("/admin/**").hasRole(RoleName.ADMIN.name()) // ADMIN 권한만 허용
                    .anyRequest().authenticated());  // 그 외 나머지 경로는 전부 인증 필요

        // cors 설정
        http.cors(cors -> cors
                .configurationSource(request -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.addAllowedOriginPattern("*"); // 모든 ip에 응답을 허용합니다.
                    corsConfiguration.addAllowedMethod("*");
                    corsConfiguration.addAllowedHeader("*");
                    corsConfiguration.setAllowCredentials(true);
                    corsConfiguration.setMaxAge(3600L);
                    return corsConfiguration;
                })
            )

        // 예외 처리
        .exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value()); // 403
                })); // 권한 없음

        // csrf 비활성화 및 jwt 필터 추가
        http.sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) // 세션 생성 정책
                .csrf(AbstractHttpConfigurer::disable); // csrf 비활성화

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class); // jwt 필터 추가

        return http.build();
    }
}