package com.example.backend.domain.member.dto;

import static com.example.backend.global.validation.ValidationGroups.*;

import com.example.backend.global.validation.annotation.PasswordMatch;
import com.example.backend.global.validation.annotation.ValidPassword;
import com.example.backend.global.validation.validator.PasswordMatchable;

import lombok.Builder;

@PasswordMatch(groups = PatternGroup.class)
@Builder
public record PasswordChangeForm(
	@ValidPassword(groups = PatternGroup.class) String originalPassword,

	@ValidPassword(groups = PatternGroup.class) String password,

	@ValidPassword(groups = PatternGroup.class) String passwordCheck) implements PasswordMatchable {

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getPasswordCheck() {
		return this.passwordCheck;
	}
}
