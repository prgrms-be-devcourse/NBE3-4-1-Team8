package com.example.backend.domain.member.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.backend.domain.cart.service.CartService;
import com.example.backend.domain.member.dto.MemberDto;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.Role;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.domain.orders.service.OrdersService;
import com.example.backend.global.redis.service.RedisService;

@ExtendWith(MockitoExtension.class)
class MemberDeleteServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private RedisService redisService;

	@Mock
	private CartService cartService;

	@Mock
	private OrdersService ordersService;

	@InjectMocks
	private MemberDeleteService memberDeleteService;

	@Test
	@DisplayName("회원 탈퇴 성공 테스트")
	void delete_success() {
		// given
		MemberDto memberDto = MemberDto.builder()
			.id(1L)
			.username("user@gmail.com")
			.nickname("user")
			.role(Role.ROLE_USER)
			.build();

		// when
		memberDeleteService.delete(memberDto);

		// then
		verify(redisService).delete("user@gmail.com");
		verify(cartService).deleteByMemberId(1L);
		verify(ordersService).deleteByMemberId(1L);
		verify(memberRepository).delete(any(Member.class));
	}
}