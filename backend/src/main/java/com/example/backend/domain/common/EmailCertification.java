package com.example.backend.domain.common;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class EmailCertification {
	private String certificationCode;
	private String verifyType;
	private String sendCount;

	@Builder
	public EmailCertification(String certificationCode, String verifyType, String sendCount) {
		this.certificationCode = certificationCode;
		this.verifyType = verifyType;
		this.sendCount = sendCount;
	}

	public EmailCertification(String certificationCode, String verifyType) {
		this.certificationCode = certificationCode;
		this.verifyType = verifyType;
		this.sendCount = "1";
	}

	public void addResendCount() {
		int count = Integer.parseInt(sendCount);
		this.sendCount = String.valueOf(count + 1);
	}

	public void setCertificationCode(String certificationCode) {
		this.certificationCode = certificationCode;
	}
}
