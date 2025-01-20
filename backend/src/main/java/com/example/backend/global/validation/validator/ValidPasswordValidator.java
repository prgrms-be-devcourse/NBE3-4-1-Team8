package com.example.backend.global.validation.validator;

import java.util.regex.Pattern;

import com.example.backend.global.validation.annotation.ValidPassword;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * ValidPasswordValidator
 * <p>비밀번호 패턴 검증 Validator</p>
 * @author Kim Dong O
 */
public class ValidPasswordValidator implements ConstraintValidator<ValidPassword, String> {
	private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*[~!@#$%^&*+=()_-])(?=.*[0-9])[^\s]{8,20}$";

	@Override
	public void initialize(ValidPassword constraintAnnotation) {
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
		return (password != null) && Pattern.matches(PASSWORD_REGEX, password);
	}
}
