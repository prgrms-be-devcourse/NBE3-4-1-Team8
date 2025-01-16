package com.example.backend.global.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.backend.global.validation.validator.ValidUsernameValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * ValidUsername
 * <p>검증할 Username 필드에 사용할 어노테이션</p>
 * <p>Validation: {@link ValidUsernameValidator}</p>
 * @author Kim Dong O
 */
@Constraint(validatedBy = ValidUsernameValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {
	String message() default "유효하지 않은 이메일 입니다.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
