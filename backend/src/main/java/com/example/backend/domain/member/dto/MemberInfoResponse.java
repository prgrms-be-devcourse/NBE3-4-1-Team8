package com.example.backend.domain.member.dto;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.entity.Member;

import lombok.Builder;

@Builder
public record MemberInfoResponse (String username, String nickname, Address address){

	public static MemberInfoResponse of(Member member){
		return MemberInfoResponse.builder()
			.username(member.getUsername())
			.nickname(member.getNickname())
			.address(member.getAddress())
			.build();
	}
}
