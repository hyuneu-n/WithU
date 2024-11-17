package com.danpoong.withu.config.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {
  private String email;
  private String nickname;
  private String refreshToken;
}
