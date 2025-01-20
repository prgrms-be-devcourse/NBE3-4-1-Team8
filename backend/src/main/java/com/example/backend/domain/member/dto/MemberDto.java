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
@Builder
public record MemberDto(Long id, String username, String nickname, String password, MemberStatus memberStatus, Role role, Address address, ZonedDateTime createdAt,
						ZonedDateTime modifiedAt) {
}
