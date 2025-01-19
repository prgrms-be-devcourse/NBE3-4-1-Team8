package com.example.backend.global.auth.dto;

import static com.example.backend.global.validation.ValidationGroups.*;

import com.example.backend.domain.common.VerifyType;
import com.example.backend.global.validation.annotation.ValidEnum;
import com.example.backend.global.validation.annotation.ValidUsername;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmailCertificationForm(
	@ValidUsername(groups = PatternGroup.class)
	String username,
	@NotBlank(message = "인증 코드는 필수 항목 입니다.", groups = NotBlankGroup.class)
	String certificationCode,
	@ValidEnum(enumClass = VerifyType.class, message = "인증 타입은 필수 항목 입니다.", groups = ValidEnumGroup.class)
	VerifyType verifyType) {
}
