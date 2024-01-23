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
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

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

        // 조회 api
        "/api/v1/playlist/user/**",
        "/api/v1/music/{playlistId}",

        // 사용자 api
        "/api/v1/member/id/{id}",
        "/api/v1/member/username/{username}",
        "/api/v1/member/nickname/available/{username}",

        // 자동완성 api
        "/api/v1/autocomplete/**",

        "/error",
    };

    @Bean
    protected SecurityFilterChain myConfig(HttpSecurity http) throws Exception {
        /* 허용 페이지 등록 */
        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(WHITE_LIST).permitAll()  // 모든 사용자 허용 경로
                        .requestMatchers("/api/v1/**").hasRole(RoleName.USER.getValue())  // 닉네임이 설정된 USER 권한만 허용
                        .anyRequest().authenticated())  // 그 외 나머지 경로는 전부 로그인 필요
//                        .anyRequest().permitAll())  // 그 외 나머지 경로는 전부 허용
                .cors(cors -> cors
                        .configurationSource(request -> {
                            CorsConfiguration corsConfiguration = new CorsConfiguration();
                            corsConfiguration.addAllowedOriginPattern("*"); // 모든 ip에 응답을 허용합니다.
                            corsConfiguration.addAllowedMethod("*");
                            corsConfiguration.addAllowedHeader("*");
                            corsConfiguration.setAllowCredentials(true);
                            corsConfiguration.setMaxAge(3600L);
                            return corsConfiguration;
                        }))
                // 예외 처리
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value()); // 403
                        })) // 권한 없음
                // 로그인
                .oauth2Login(oauth2 -> oauth2
                        .loginPage(envBean.getLoginPage()) // 로그인 페이지
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(OAuth2Service)) // 유저 정보 가져오기
                        .successHandler(loginSuccessHandler) // 로그인 성공
                        .failureHandler(loginFailureHandler) // 로그인 실패
                        .permitAll()
                )
                // 로그아웃
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 비활성화
                .csrf(AbstractHttpConfigurer::disable); // csrf 비활성화

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}