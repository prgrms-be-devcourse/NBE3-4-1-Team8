package com.example.backend.global.validation.validator;

/**
 * PasswordMatchable
 * <p>PasswordMatchValidator 사용을 위해 정의한 인터페이스</p>
 * @author Kim Dong O
 */
public interface PasswordMatchable {
	String getPassword();

	String getPasswordCheck();
}
