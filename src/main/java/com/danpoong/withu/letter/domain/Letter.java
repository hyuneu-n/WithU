package com.danpoong.withu.letter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import com.danpoong.withu.common.BaseTimeEntity;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Letter extends BaseTimeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "letter_id")
  private Long id;

  @Column(nullable = false)
  private Long familyId;

  private Long scheduleId;

  @Column(nullable = false)
  private Long senderId;

  @Column(nullable = false)
  private Long receiverId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LetterType letterType;

  private String keyName;

  private String textContent;

  @Builder.Default
  private Boolean isSaved = false;

  @Column(nullable = false)
  private Boolean isLiked;

  //    @Column(nullable = false, updatable = false)
  //    @CreationTimestamp
  //    private LocalDateTime createdAt;
  public void setSaved() {
    this.isSaved = true;
  }

  public void changeIsLiked() {
    this.isLiked = this.isLiked == null ? true : !this.isLiked;
  }
}
