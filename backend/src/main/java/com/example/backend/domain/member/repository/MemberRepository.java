package com.example.backend.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.domain.member.entity.Member;


/**
 * MemberJpaRepository
 * <p>MemberJpaRepository 입니다.</p>
 * @author Kim Dong O
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
	Member save(Member member);
}
