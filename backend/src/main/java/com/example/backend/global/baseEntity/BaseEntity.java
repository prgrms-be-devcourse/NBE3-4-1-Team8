package com.example.backend.global.baseEntity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

/**
 * BaseEntity
 * <p>엔티티 생성, 수정 일자를 관리하는 BaseEntity 입니다.</p>
 * @author Kim Dong O
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	/**
	 * 생성일시
	 */
	@CreatedDate
	@Column(name = "created_at")
	protected LocalDateTime createdAt;

	/**
	 * 수정일시
	 */
	@LastModifiedDate
	@Column(name = "modified_at")
	protected LocalDateTime modifiedAt;

}