package com.danpoong.withu.user.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class UserRegisterRequest {
  private String nickname;
  private String profileImage;
  private LocalDate birthday;
}
