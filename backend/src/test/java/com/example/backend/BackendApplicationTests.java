package com.example.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.example.backend.global.config.CorsConfig;
import com.example.backend.global.config.MailConfig;
import com.example.backend.global.config.RedisConfig;
import com.example.backend.global.config.TestSecurityConfig;

@Import({MailConfig.class, CorsConfig.class, TestSecurityConfig.class, RedisConfig.class})
@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
