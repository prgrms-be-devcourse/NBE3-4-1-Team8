package com.example.backend.global.validation.validator;

import java.util.regex.Pattern;

import com.example.backend.global.validation.annotation.ValidNickname;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * ValidNicknameValidator
 * <p>닉네임 패턴 검증 Validator</p>
 * @author Kim Dong O
 */
public class ValidNicknameValidator implements ConstraintValidator<ValidNickname, String> {
	private static final String NICKNAME_REGEX = "^[가-힣a-zA-Z0-9]{2,}$";

	@Override
	public void initialize(ValidNickname constraintAnnotation) {
	}

	@Override
	public boolean isValid(String nickname, ConstraintValidatorContext constraintValidatorContext) {
		return Pattern.matches(NICKNAME_REGEX, nickname);
	}
}
