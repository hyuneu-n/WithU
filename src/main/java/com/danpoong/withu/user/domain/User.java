package com.danpoong.withu.user.domain;

import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.danpoong.withu.family.domain.Family;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false)
  private String nickname;

  @Column private String profileImage;

  @Column private LocalDate birthday; // 추후 마이페이지에서 설정 예정 -> 카카오 권한 없음!

  @ManyToOne
  @JoinColumn(name = "familyId", nullable = true)
  private Family family;

  @Column(nullable = false)
  private String role;

  @Column private String refreshToken;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDate createdAt;

  @Column(nullable = false)
  private Boolean isDeleted = false;
}
