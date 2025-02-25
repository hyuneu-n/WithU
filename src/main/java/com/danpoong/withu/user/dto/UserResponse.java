package com.danpoong.withu.user.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
  private String email;
  private String nickname;
  private String profileImage;
  private LocalDate birthday;
  private String role;
  private Long familyId;
}
