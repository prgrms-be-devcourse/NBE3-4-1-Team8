package com.example.backend.global.auth.service;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.example.backend.global.redis.service.RedisService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisService redisService;

    public void  saveRefreshToken(String username, String refreshToken) {
        //timeout은 분 기준으로 추가
        redisService.setData(username, refreshToken, 10080);
    }

    public String getRefreshToken(String username) {
        return redisService.getData(username);
    }

    public void deleteRefreshToken(String username) {
        redisService.delete(username);
    }

    public boolean isValidRefreshToken(String username, String refreshToken) {
        return Objects.equals(redisTemplate.opsForValue().get(username), refreshToken);
    }
}
