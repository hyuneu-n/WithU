package com.danpoong.withu.config.auth.dto;

public interface OAuth2Response {
  String getProvider();

  String getProviderId();

  String getEmail();

  String getNickname();

  String getProfileUrl();
}
