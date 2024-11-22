package com.danpoong.withu.common;

import java.time.LocalDateTime;

import lombok.Getter;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

  @CreatedDate private LocalDateTime createdDate;

  @LastModifiedDate private LocalDateTime modifiedDate;
}
