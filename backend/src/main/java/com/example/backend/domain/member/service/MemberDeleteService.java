package com.example.backend.domain.member.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.domain.cart.service.CartService;
import com.example.backend.domain.member.dto.MemberDto;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.repository.MemberRepository;
import com.example.backend.domain.orders.service.OrdersService;
import com.example.backend.global.redis.service.RedisService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberDeleteService {

	private final MemberRepository memberRepository;
	private final RedisService redisService;
	private final CartService cartService;
	private final OrdersService ordersService;

	public void delete(MemberDto memberDto) {
		redisService.delete(memberDto.username());
		SecurityContextHolder.clearContext();
		cartService.deleteByMemberId(memberDto.id());
		ordersService.deleteByMemberId(memberDto.id());
		memberRepository.delete(Member.from(memberDto));
	}
}
