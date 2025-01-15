package com.example.backend.domain.member.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.global.config.JpaAuditingConfig;

@DataJpaTest
@Import(JpaAuditingConfig.class)
class MemberRepositoryTest {
	private final MemberRepository memberRepository;

	@Autowired
	MemberRepositoryTest(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@DisplayName("회원 저장 성공 테스트")
	@Test
	void save_success() {
		//given
		Address givenAddress = Address.builder()
			.city("testCity")
			.detail("testDetail")
			.district("testDitrict")
			.country("testCountry")
			.build();

		Member givenMember = Member.builder()
			.username("testEmail@naver.com")
			.nickname("testNickname")
			.password("testPassword")
			.address(givenAddress)
			.role(Role.ROLE_USER)
			.build();

		//when
		Member savedMember = memberRepository.save(givenMember);

		//then
		assertThat(savedMember.getId()).isNotNull();
		assertThat(savedMember.getUsername()).isEqualTo(givenMember.getUsername());
		assertThat(savedMember.getNickname()).isEqualTo(givenMember.getNickname());
		assertThat(savedMember.getPassword()).isEqualTo(givenMember.getPassword());
		assertThat(savedMember.getRole()).isEqualTo(givenMember.getRole());
		assertThat(savedMember.getAddress()).isEqualTo(givenMember.getAddress());
		assertThat(savedMember.getCreatedAt()).isNotNull();
		assertThat(savedMember.getModifiedAt()).isNotNull();
	}

	@DisplayName("회원 조회 성공 테스트")
	@Test
	void find_id_success() {
		//given
		Address givenAddress = Address.builder()
			.city("testCity1")
			.detail("testDetail")
			.district("testDitrict")
			.country("testCountry")
			.build();

		Member givenMember = Member.builder()
			.username("testEmail1@naver.com")
			.nickname("testNickname1")
			.password("testPassword1")
			.address(givenAddress)
			.role(Role.ROLE_USER)
			.build();

		Member savedMember = memberRepository.save(givenMember);

		//when
		Member findMember = memberRepository.findById(savedMember.getId()).get();

		//then
		assertThat(findMember).isEqualTo(savedMember);
	}

	@DisplayName("회원 이메일 존재하는지 조회 성공 테스트")
	@Test
	void exists_username_success() {
		//given
		Address givenAddress = Address.builder()
			.city("testCity1")
			.detail("testDetail")
			.district("testDitrict")
			.country("testCountry")
			.build();

		Member givenMember = Member.builder()
			.username("testEmail2@naver.com")
			.nickname("testNickname2")
			.password("testPassword1")
			.address(givenAddress)
			.role(Role.ROLE_USER)
			.build();

		Member savedMember = memberRepository.save(givenMember);

		//when
		boolean existsByUsername = memberRepository.existsByUsername(savedMember.getUsername());

		//then
		assertThat(existsByUsername).isTrue();
	}

}