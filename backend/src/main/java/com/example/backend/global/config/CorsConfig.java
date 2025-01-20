package com.example.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);   // 클라이언트가 자격 증명을 함께 전송할 수 있도록 허용
        config.addAllowedOrigin("http://localhost:3000");   // 클라이언트 도메인
        config.addAllowedHeader("*");   // 서버에서 클라이언트로 보낼때 모든 헤더를 허용
        config.addExposedHeader("Authorization");   // 클라이언트에서 Authorization 헤더에 접근 허용
        config.addAllowedMethod("*");   // 모든 메서드 허용
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
