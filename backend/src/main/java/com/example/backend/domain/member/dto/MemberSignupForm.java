package com.example.backend.domain.member.dto;

import static com.example.backend.global.validation.ValidationGroups.*;

import com.example.backend.global.validation.annotation.PasswordMatch;
import com.example.backend.global.validation.annotation.ValidNickname;
import com.example.backend.global.validation.annotation.ValidPassword;
import com.example.backend.global.validation.annotation.ValidUsername;
import com.example.backend.global.validation.validator.PasswordMatchable;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * MemberForm
 * <p>회원가입시 사용하는 Request 객체 입니다.</p>
 * @param username 이메일
 * @param nickname 닉네임
 * @param password 비밀번호
 * @param passwordCheck 비밀번호 확인
 * @param city 시
 * @param district 지역 구
 * @param country 도로명 주소
 * @param detail 상세 주소
 * @author Kim Dong O
 */
@Builder
@PasswordMatch(groups = PatternGroup.class)
public record MemberSignupForm(
	@ValidUsername(groups = PatternGroup.class)
	String username,

	@ValidNickname(groups = PatternGroup.class)
	String nickname,

	@ValidPassword(groups = PatternGroup.class)
	String password,

	@ValidPassword(groups = PatternGroup.class)
	String passwordCheck,

	@NotBlank(message = "도시는 필수 항목 입니다.", groups = NotBlankGroup.class)
	String city,

	@NotBlank(message = "지역 구는 필수 항목 입니다.", groups = NotBlankGroup.class)
	String district,

	@NotBlank(message = "도로명 주소는 필수 항목 입니다.", groups = NotBlankGroup.class)
	String country,

	@NotBlank(message = "상세 주소는 필수 항목 입니다.", groups = NotBlankGroup.class)
	String detail)
	implements PasswordMatchable {

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getPasswordCheck() {
		return this.passwordCheck;
	}
}
