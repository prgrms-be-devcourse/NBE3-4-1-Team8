package com.example.backend.domain.member.repository;

import org.springframework.stereotype.Repository;

import com.example.backend.domain.member.dto.MemberDto;
import com.example.backend.domain.member.entity.Member;

import lombok.RequiredArgsConstructor;

/**
 * MemberRepositoryImpl
 * <p>MemberRepository 구현체 입니다.</p>
 * @author Kim Dong O
 */
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
	private final MemberJpaRepository memberJpaRepository;

	@Override
	public MemberDto save(MemberDto memberDto) {
		return memberJpaRepository.save(Member.from(memberDto)).toModel();
	}
}
