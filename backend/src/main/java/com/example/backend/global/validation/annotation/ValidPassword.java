package com.example.backend.global.validation.annotation;

import static com.example.backend.global.validation.ValidationGroups.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.example.backend.global.validation.ValidationGroups;
import com.example.backend.global.validation.validator.ValidPasswordValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * ValidPassword
 * <p>검증할 Password 필드에 사용할 어노테이션</p>
 * <p>Validation: {@link ValidPasswordValidator}</p>
 * @author Kim Dong O
 */
@Constraint(validatedBy = ValidPasswordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
	String message() default "공백 없이 비밀번호는 최소 8자리, 최대 20자리이며 대소문자, 숫자, 특수문자 1개씩 필수 입력해야 합니다.";

	Class<?>[] groups() default PatternGroup.class;

	Class<? extends Payload>[] payload() default {};
}
