package com.example.backend.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Address
 * <p>주소지 정보를 관리하는 클래스 입니다.</p>
 * @author Kim Dong O
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
	@Column(nullable = false)
	private String city; //시

	@Column(nullable = false)
	private String district; //구

	@Column(nullable = false)
	private String country; //도로명 주소

	@Column(nullable = false)
	private String detail; //상세 주소

	@Builder
	public Address(String city, String district, String country, String detail) {
		this.city = city;
		this.district = district;
		this.country = country;
		this.detail = detail;
	}
}
