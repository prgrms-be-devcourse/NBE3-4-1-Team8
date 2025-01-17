package com.example.backend.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * TestSecurityConfig
 * <p></p>
 *
 * @author 100mi
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@Profile("test")
public class TestSecurityConfig {

    private final CorsConfig corsConfig;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .addFilter(corsConfig.corsFilter())
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/v1/members/join").permitAll()
                        .requestMatchers("/api/v1/members/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/auth/login", "/api/v1/auth/code", "api/v1/auth/verify").permitAll()
                        .requestMatchers("/api/v1/auth/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
                        .requestMatchers("/api/v1/products/**").hasAnyRole("ADMIN")
                        .requestMatchers("/api/v1/orders/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/api/v1/carts/**").hasAnyRole("USER", "ADMIN"))
                ;
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
