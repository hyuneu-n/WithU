package com.danpoong.withu.schedule.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.danpoong.withu.family.domain.Family;
import com.danpoong.withu.user.domain.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class Schedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long scheduleId;

  @ManyToOne
  @JoinColumn(name = "familyId", nullable = true)
  private Family family;

  @ManyToOne
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @Column(nullable = false)
  private String title;

  @Column(nullable = true)
  private String memo;

  @Column(nullable = false)
  private LocalDate date;

  @Column(nullable = false)
  private String author;

  @Column(nullable = false)
  private Integer dday;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = true)
  private LocalDateTime updatedAt;

  @Builder.Default
  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted = false;

  // D-Day 계산
  public int calculateDDay() {
    return (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), this.date);
  }
}
