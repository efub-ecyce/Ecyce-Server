package com.ecyce.karma.global.config;

import com.ecyce.karma.domain.auth.errorHandler.CustomJwtAuthenticationEntryPoint;
import com.ecyce.karma.domain.auth.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;

    // 인증이 필요없는 URL 패턴 목록을 정의
    private static final String[] AUTH_WHITELIST = {
            "/login/oauth2/kakao", // 로그인
            "/product/{productId}", // 상품 상세 조회
            "/product", // 전체 상품 조회
            "/product/categorycode/*", // 카테고리별 조회
            "/ws/**",
            "/reviews/{reviewId}", // 리뷰 상세 조회
            "/product/{productId}/reviews", // 상품별 리뷰 조회
            "/api/s3/**" // s3 엔드포인트 추가
    };

    // cors 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "https://jiangxy.github.io", "http://localhost:3000", "https://api.ecyce-karma.n-e.kr", "https://ecyce.netlify.app"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // http의 특정 보안 구성을 비활성화
        http
                .csrf(csrf -> csrf.disable()) //csrf 공격을 대비하기 위한 csrf 토큰 disable 하기
                .formLogin(formLogin -> formLogin.disable()) //form login 비활성화 jwt를 사용하고 있으므로 폼 기반 로그인은 필요하지 않다.
                .httpBasic(httpBasic -> httpBasic.disable())//http 기본 인증은 사용자 이름과 비밀번호를 평문으로 전송하기 때문에 보안적으로 취약, 기본 인증을 비활성화 하고 있음
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> { // 세션 사용 안함
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .exceptionHandling(exception ->
                {
                    exception.authenticationEntryPoint(customJwtAuthenticationEntryPoint); // 인증 실패 시
                });

        http.authorizeHttpRequests(auth -> {
                    // 여기에 정의된 URL 패턴은 모든 사용자에게 접근 허용하고, 그외의 모든 요청은 인증으로 요구함
                    auth.requestMatchers(AUTH_WHITELIST).permitAll();
                    auth.anyRequest().authenticated();
                })
                // JWT 인증 필터 추가 - UsernamePasswordAuthentication 클래스 앞에 jwtAuthenticationFilter를 등록
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
