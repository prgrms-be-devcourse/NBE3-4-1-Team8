package com.example.backend.domain.member.dto;

import java.time.ZonedDateTime;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.entity.MemberStatus;
import com.example.backend.domain.member.entity.Role;

import lombok.Builder;

/**
 * MemberDto
 * <p>Member Dto 입니다.</p>
 * @param id
 * @param username
 * @param nickname
 * @param password
 * @param memberStatus
 * @param role
 * @param address
 * @param createdAt
 * @param modifiedAt
 * @author Kim Dong O
 */
public record MemberDto(Long id, String username, String nickname, String password, MemberStatus memberStatus, Role role, Address address, ZonedDateTime createdAt,
						ZonedDateTime modifiedAt) {

	@Builder
	public MemberDto(Long id, String username, String nickname, String password, MemberStatus memberStatus, Role role, Address address, ZonedDateTime createdAt,
		ZonedDateTime modifiedAt) {
		this.id = id;
		this.username = username;
		this.nickname = nickname;
		this.password = password;
		this.memberStatus = memberStatus;
		this.role = role;
		this.address = address;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}

	public MemberDto verify() {
		return MemberDto.builder()
			.id(this.id)
			.username(username)
			.nickname(nickname)
			.password(password)
			.memberStatus(MemberStatus.ACTIVE)
			.role(this.role)
			.address(this.address)
			.createdAt(this.createdAt)
			.modifiedAt(this.modifiedAt)
			.build();
	}
}
