package com.example.backend.domain.member.dto;

import com.example.backend.global.validation.ValidationGroups;
import com.example.backend.global.validation.annotation.ValidNickname;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record MemberModifyForm (
	@ValidNickname(groups = ValidationGroups.PatternGroup.class)
	String nickname,

	@NotBlank(message = "도시는 필수 항목 입니다.", groups = ValidationGroups.NotBlankGroup.class)
	String city,

	@NotBlank(message = "지역 구는 필수 항목 입니다.", groups = ValidationGroups.NotBlankGroup.class)
	String district,

	@NotBlank(message = "도로명 주소는 필수 항목 입니다.", groups = ValidationGroups.NotBlankGroup.class)
	String country,

	@NotBlank(message = "상세 주소는 필수 항목 입니다.", groups = ValidationGroups.NotBlankGroup.class)
	String detail){

}
