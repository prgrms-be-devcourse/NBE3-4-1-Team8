package com.example.backend.domain.member.conveter;

import com.example.backend.domain.common.Address;
import com.example.backend.domain.member.dto.MemberDto;
import com.example.backend.domain.member.dto.MemberInfoResponse;
import com.example.backend.domain.member.dto.MemberModifyForm;
import com.example.backend.domain.member.entity.Member;

public final class MemberConverter {

	public static MemberInfoResponse from(Member member){
		return MemberInfoResponse.builder()
			.username(member.getUsername())
			.nickname(member.getNickname())
			.address(member.getAddress())
			.build();
	}

	public static MemberDto of(MemberDto memberDto, MemberModifyForm memberModifyForm){
		return MemberDto.builder()
			.id(memberDto.id())
			.username(memberDto.username())
			.nickname(memberModifyForm.nickname())
			.password(memberDto.password())
			.memberStatus(memberDto.memberStatus())
			.role(memberDto.role())
			.address(toAddress(memberModifyForm))
			.createdAt(memberDto.createdAt())
			.modifiedAt(memberDto.modifiedAt())
			.build();
	}

	private static Address toAddress(MemberModifyForm memberModifyForm) {
		return Address.builder()
			.city(memberModifyForm.city())
			.district(memberModifyForm.district())
			.country(memberModifyForm.country())
			.detail(memberModifyForm.detail())
			.build();
	}
}
