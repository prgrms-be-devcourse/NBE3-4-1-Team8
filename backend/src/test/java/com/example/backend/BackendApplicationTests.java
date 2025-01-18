package com.example.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.example.backend.global.config.CorsConfig;
import com.example.backend.global.config.TestSecurityConfig;

@Import({CorsConfig.class, TestSecurityConfig.class})
@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
