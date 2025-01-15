package com.example.backend.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.backend.domain.member.entity.Member;


/**
 * MemberJpaRepository
 * <p>MemberJpaRepository 입니다.</p>
 * @author Kim Dong O
 */
public interface MemberRepository extends JpaRepository<Member, Long> {
	/**
	 * 회원 Username이 중복인지 체크하는 메서드
	 * @param username
	 * @return Username이 중복이라면 true, 중복이 아니라면 false
	 */
	boolean existsByUsername(String username);

	/**
	 * 회원 Nickname이 중복인지 체크하는 메서드
	 * @param nickname
	 * @return Nickname이 중복이라면 true, 중복이 아니라면 false
	 */
	boolean existsByNickname(String nickname);
}
