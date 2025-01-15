package com.example.backend.domain.member.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.entity.Role;

import lombok.Builder;

public record MemberDto(Long id, String username, String nickname, String password, Role role, Address address, ZonedDateTime createdAt,
		ZonedDateTime modifiedAt) {

	@Builder
	public MemberDto(Long id, String username, String nickname, String password, Role role, Address address, ZonedDateTime createdAt,
		ZonedDateTime modifiedAt) {
		this.id = id;
		this.username = username;
		this.nickname = nickname;
		this.password = password;
		this.role = role;
		this.address = address;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}
}
