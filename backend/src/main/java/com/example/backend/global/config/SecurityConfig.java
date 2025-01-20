package com.example.backend.global.config;

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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.backend.global.auth.filter.JwtAuthorizationFilter;
import com.example.backend.global.auth.filter.RefreshTokenFilter;
import com.example.backend.global.auth.jwt.JwtProvider;
import com.example.backend.global.auth.jwt.JwtUtils;
import com.example.backend.global.auth.service.CookieService;
import com.example.backend.global.auth.service.CustomUserDetailsService;
import com.example.backend.global.auth.service.RefreshTokenService;
import com.example.backend.global.auth.util.FilterUtils;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Profile("!test")
public class SecurityConfig {

    private final CorsConfig corsConfig;
    private final JwtProvider jwtProvider;
    private final JwtUtils jwtUtils;
    private final FilterUtils filterUtils;
    private final CookieService cookieService;
    private final RefreshTokenService refreshTokenService;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS))
            .addFilter(corsConfig.corsFilter())
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers(HttpMethod.GET, "/api/v1/products").permitAll()
                .requestMatchers("/api/v1/members/join", "/api/v1/auth/verify", "/api/v1/auth/login", "/api/v1/auth/code").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/products/{id}").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/members/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/auth/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/products/**").hasAnyRole("ADMIN")
                .requestMatchers("/api/v1/orders/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/v1/carts/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().permitAll())
            .addFilterBefore(new JwtAuthorizationFilter(jwtUtils, filterUtils, cookieService, customUserDetailsService),
                UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(new RefreshTokenFilter(jwtProvider, jwtUtils, filterUtils, cookieService,
                refreshTokenService, customUserDetailsService), JwtAuthorizationFilter.class);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
