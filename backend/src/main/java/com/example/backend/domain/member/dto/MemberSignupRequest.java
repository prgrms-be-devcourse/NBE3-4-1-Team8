package com.example.backend.domain.member.dto;

import static com.example.backend.global.validation.ValidationGroups.*;

import com.example.backend.global.validation.annotation.PasswordMatch;
import com.example.backend.global.validation.annotation.ValidNickname;
import com.example.backend.global.validation.annotation.ValidPassword;
import com.example.backend.global.validation.annotation.ValidUsername;
import com.example.backend.global.validation.validator.PasswordMatchable;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@PasswordMatch(groups = PatternGroup.class)
public record MemberSignupRequest(
	@ValidUsername(groups = PatternGroup.class)
	String username,

	@ValidNickname(groups = PatternGroup.class)
	String nickname,

	@ValidPassword(groups = PatternGroup.class)
	String password,

	@ValidPassword(groups = PatternGroup.class)
	String passwordCheck,

	@NotBlank(message = "인증코드는 필수 항목입니다.", groups = NotBlankGroup.class)
	String verifyCode,

	@NotBlank(message = "도시는 필수 항목입니다.", groups = NotBlankGroup.class)
	String city,

	@NotBlank(message = "지역구는 필수 항목입니다.", groups = NotBlankGroup.class)
	String district,

	@NotBlank(message = "도로명 주소는 필수 항목입니다.", groups = NotBlankGroup.class)
	String country,

	@NotBlank(message = "상세주소는 필수 항목 입니다.", groups = NotBlankGroup.class)
	String detail)
	implements PasswordMatchable {

	@Builder
	public MemberSignupRequest(String username, String nickname, String password, String passwordCheck,
		String verifyCode, String city, String district, String country, String detail) {
		this.username = username;
		this.nickname = nickname;
		this.password = password;
		this.passwordCheck = passwordCheck;
		this.verifyCode = verifyCode;
		this.city = city;
		this.district = district;
		this.country = country;
		this.detail = detail;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getPasswordCheck() {
		return this.passwordCheck;
	}
}
