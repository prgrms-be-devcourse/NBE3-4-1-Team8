package com.example.backend.domain.member.entity;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
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
	@Column(length = 20, nullable = false)
	private MemberStatus memberStatus;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private Role role;

	@Embedded
	private Address address;

	@Builder(toBuilder = true)
	protected Member(Long id, String username, String nickname, String password, MemberStatus memberStatus, Role role,
		Address address, ZonedDateTime createdAt,
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

	public static Member from(MemberDto memberDto) {
		return Member.builder()
			.id(memberDto.id())
			.username(memberDto.username())
			.nickname(memberDto.nickname())
			.password(memberDto.password())
			.memberStatus(memberDto.memberStatus())
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
			.memberStatus(this.memberStatus)
			.role(this.role)
			.address(this.address)
			.createdAt(this.createdAt)
			.modifiedAt(this.modifiedAt)
			.build();
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void verify() {
		this.memberStatus = MemberStatus.ACTIVE;
	}

	public static String createTemporaryPassword() {
		final String SPECIAL_CHARACTERS = "~!@#$%^&*+=()_-";
		String uuid = UUID.randomUUID().toString().replace("-", "");
		StringBuilder password = new StringBuilder();

		char randomLetter = (char)('a' + ThreadLocalRandom.current().nextInt(26));
		password.append(randomLetter);

		char randomSpecialChar = SPECIAL_CHARACTERS.charAt(
			ThreadLocalRandom.current().nextInt(SPECIAL_CHARACTERS.length()));
		password.append(randomSpecialChar);

		char randomDigit = (char)('0' + ThreadLocalRandom.current().nextInt(10));
		password.append(randomDigit);

		while (password.length() < 8) {
			char randomChar = uuid.charAt(ThreadLocalRandom.current().nextInt(uuid.length()));
			password.append(randomChar);
		}

		return password.toString();
	}
}
