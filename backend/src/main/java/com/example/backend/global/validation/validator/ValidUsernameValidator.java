package com.example.backend.global.validation.validator;

import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.example.backend.global.validation.annotation.ValidUsername;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String> {
	private static final String USERNAME_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

	@Override
	public void initialize(ValidUsername constraintAnnotation) {
	}

	@Override
	public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {

		return StringUtils.hasText(username) && Pattern.matches(USERNAME_REGEX, username);
	}
}
