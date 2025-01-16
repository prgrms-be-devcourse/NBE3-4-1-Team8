package com.example.backend.global.validation.validator;



import com.example.backend.global.validation.annotation.PasswordMatch;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * PasswordMatchValidator
 * <p>password, passwordCheck 두개가 동일한지 검증하는 Validation</p>
 * @author Kim Dong O
 */
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, PasswordMatchable> {
	private PasswordMatch passwordMatch;

	@Override
	public boolean isValid(PasswordMatchable passwordMatchable, ConstraintValidatorContext constraintValidatorContext) {
		return (passwordMatchable.getPasswordCheck() != null) && (passwordMatchable.getPassword()
			.equals(passwordMatchable.getPasswordCheck()));
	}

	@Override
	public void initialize(PasswordMatch constraintAnnotation) {
		this.passwordMatch = constraintAnnotation;
	}
}
