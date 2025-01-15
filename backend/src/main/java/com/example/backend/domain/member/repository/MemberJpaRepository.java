package com.example.backend.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.domain.member.entity.Member;

/**
 * MemberJpaRepository
 * <p>MemberJpaRepository 입니다.</p>
 * @author Kim Dong O
 */
public interface MemberJpaRepository extends JpaRepository<Member, Long> {
}
