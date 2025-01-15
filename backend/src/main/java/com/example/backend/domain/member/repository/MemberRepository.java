package com.example.backend.domain.member.repository;

import com.example.backend.domain.member.dto.MemberDto;

public interface MemberRepository {
	/**
	 * @implSpec MemberDto를 파라미터로 받아 저장 후 MemberDto로 반환합니다.
	 * @param memberDto
	 * @return {@link MemberDto}
	 */
	MemberDto save(MemberDto memberDto);
}
