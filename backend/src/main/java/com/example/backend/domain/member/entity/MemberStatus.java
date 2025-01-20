package com.example.backend.domain.member.entity;

/**
 * 멤버 상태를 나타내는 Enum 입니다,
 * <p>PENDING -> 이메일 인증 X <br>
 * ACTIVE -> 이메일 인증 완료</p>
 * @author Kim Dong O
 */
public enum MemberStatus {
	PENDING, ACTIVE
}
