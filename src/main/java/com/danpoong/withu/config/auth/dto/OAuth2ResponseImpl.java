package com.danpoong.withu.config.auth.dto;

import lombok.Getter;

import com.danpoong.withu.user.domain.User;

@Getter
public class OAuth2ResponseImpl implements OAuth2Response {
  private final String provider = "kakao"; // Provider 고정 (예: Kakao)
  private final String providerId;
  private final String email;
  private final String nickname;
  private final String profileUrl;

  public OAuth2ResponseImpl(String providerId, String email, String nickname, String profileUrl) {
    this.providerId = providerId;
    this.email = email;
    this.nickname = nickname;
    this.profileUrl = profileUrl;
  }

  public static OAuth2ResponseImpl fromUser(User user) {
    return new OAuth2ResponseImpl(
        user.getId().toString(), // 예시로 user ID를 providerId로 설정
        user.getEmail(),
        user.getNickname(),
        null // Profile URL은 없을 경우 null
        );
  }
}
