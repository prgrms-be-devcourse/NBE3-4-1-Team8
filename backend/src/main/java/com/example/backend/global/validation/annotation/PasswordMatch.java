package com.example.backend.global.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.backend.global.validation.validator.PasswordMatchValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * PasswordMatch
 * <p>검증할 Object 에 사용할 어노테이션<br>
 * 반드시 아래 구현체를 상속받아 객체를 구현한 후에 사용해야 합니다. <br>
 * 구현체 : {@link com.example.backend.global.validation.validator.PasswordMatchable} <br>
 * Validation: {@link PasswordMatchValidator}</p>
 * @author Kim Dong O
 */
@Constraint(validatedBy = PasswordMatchValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordMatch {
	String message() default "비밀번호와 비밀번호 확인이 일치하지 않습니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
