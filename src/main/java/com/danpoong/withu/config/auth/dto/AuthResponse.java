package com.danpoong.withu.config.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
  private String accessToken;
  private String refreshToken; // 추가

  // 기존 생성자
  public AuthResponse(String accessToken) {
    this.accessToken = accessToken;
  }

  // 새로 추가된 생성자
  public AuthResponse(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}
