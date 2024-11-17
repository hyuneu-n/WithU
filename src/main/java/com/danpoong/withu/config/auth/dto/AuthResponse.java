package com.danpoong.withu.config.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
  private String accessToken;

  public AuthResponse(String accessToken) {
    this.accessToken = accessToken;
  }
}
