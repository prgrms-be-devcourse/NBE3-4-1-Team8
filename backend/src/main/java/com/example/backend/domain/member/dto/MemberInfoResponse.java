package com.example.backend.domain.member.dto;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.entity.Member;
import com.example.backend.domain.member.entity.MemberStatus;

import lombok.Builder;

@Builder
public record MemberInfoResponse (String username, String nickname, MemberStatus memberStatus, Address address){

	public static MemberInfoResponse of(Member member){
		return MemberInfoResponse.builder()
			.username(member.getUsername())
			.nickname(member.getNickname())
			.memberStatus(member.getMemberStatus())
			.address(member.getAddress())
			.build();
	}
}
