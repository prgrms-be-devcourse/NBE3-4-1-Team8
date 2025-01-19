package com.example.backend.global.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Team8 1차 프로젝트", version = "v1"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class SpringDocConfig {

	@Bean
	public GroupedOpenApi api() {
		return GroupedOpenApi.builder()
			.group("apiV1")
			.pathsToMatch("/api/**")
			.build();
	}

	@Bean
	public GroupedOpenApi groupApiMembers() {
		return GroupedOpenApi.builder()
			.group("members")
			.pathsToMatch("/api/v1/members/**")
			.build();
	}

	@Bean
	public GroupedOpenApi groupApiProducts() {
		return GroupedOpenApi.builder()
			.group("products")
			.pathsToMatch("/api/v1/products/**")
			.build();
	}

	@Bean
	public GroupedOpenApi groupApiOrders() {
		return GroupedOpenApi.builder()
			.group("orders")
			.pathsToMatch("/api/v1/orders/**")
			.build();
	}

	@Bean
	public GroupedOpenApi groupApiCarts() {
		return GroupedOpenApi.builder()
			.group("carts")
			.pathsToMatch("/api/v1/carts/**")
			.build();
	}

	@Bean
	public GroupedOpenApi groupApiAuth() {
		return GroupedOpenApi.builder()
			.group("auth")
			.pathsToMatch("/api/v1/auth/**")
			.build();
	}
}
