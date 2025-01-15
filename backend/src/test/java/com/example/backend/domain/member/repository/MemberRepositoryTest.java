package com.example.backend.domain.member.repository;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

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

	@Test
	void save() {
		//given
		Address givenAddress = Address.builder()
			.city("testCity")
			.detail("testDetail")
			.ditrict("testDitrict")
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
}