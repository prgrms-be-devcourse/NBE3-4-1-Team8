package com.example.backend.domain.member.entity;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.dto.MemberDto;
import com.example.backend.global.baseEntity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false, unique = true)
	private String nickname;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	private Role role;

	@Embedded
	private Address address;

	@Builder
	protected Member(Long id, String username, String nickname, String password, Role role, Address address, LocalDateTime createdAt,
		LocalDateTime modifiedAt) {
		this.id = id;
		this.username = username;
		this.nickname = nickname;
		this.password = password;
		this.role = role;
		this.address = address;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}

	public static Member from(MemberDto memberDto) {
		return Member.builder()
			.id(memberDto.id())
			.username(memberDto.username())
			.nickname(memberDto.nickname())
			.password(memberDto.password())
			.role(memberDto.role())
			.address(memberDto.address())
			.createdAt(memberDto.createdAt())
			.modifiedAt(memberDto.modifiedAt())
			.build();
	}

	public MemberDto toModel() {
		return MemberDto.builder()
			.id(this.id)
			.username(this.username)
			.nickname(this.nickname)
			.password(this.password)
			.role(this.role)
			.address(this.address)
			.createdAt(this.createdAt)
			.modifiedAt(this.modifiedAt)
			.build();
	}
}
