package com.example.backend.domain.member.conveter;

import com.example.backend.domain.member.dto.MemberInfoResponse;
import com.example.backend.domain.member.entity.Member;

public final class MemberConverter {

	public static MemberInfoResponse from(Member member){
		return MemberInfoResponse.builder()
			.username(member.getUsername())
			.nickname(member.getNickname())
			.address(member.getAddress())
			.build();
	}
}
