package com.danpoong.withu.config.auth.dto;

import com.danpoong.withu.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User implements OAuth2User {

  private final User user;

  public CustomOAuth2User(User user) {
    this.user = user;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return Map.of(
            "email", user.getEmail(),
            "nickname", user.getNickname(),
            "role", user.getRole()
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null; // 필요한 경우 권한 정보를 반환
  }

  @Override
  public String getName() {
    return user.getEmail();
  }
}
