package com.example.backend.global.baseEntity;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;

/**
 * BaseEntity
 * <p>엔티티 생성, 수정 일자를 관리하는 BaseEntity 입니다.</p>
 * @author Kim Dong O
 */
@Getter
@MappedSuperclass
public abstract class BaseEntity {

	/**
	 * 생성일시
	 */
	@Column(name = "created_at")
	protected ZonedDateTime createdAt;

	/**
	 * 수정일시
	 */
	@Column(name = "modified_at")
	protected ZonedDateTime modifiedAt;

	@PrePersist
	public void prePersist() {
		this.createdAt = ZonedDateTime.now();
		this.modifiedAt = ZonedDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.modifiedAt = ZonedDateTime.now();
	}
}