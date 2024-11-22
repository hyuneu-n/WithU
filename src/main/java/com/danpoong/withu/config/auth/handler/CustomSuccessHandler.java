package com.danpoong.withu.config.auth.handler;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;

  @Value("${front-url}")
  private String frontUrl;

  @Value("${redirect-url-suffix}")
  private String redirectUrlSuffix;

  private static final String DEFAULT_ROLE = "ROLE_USER";

  @Override
  public void onAuthenticationSuccess(
          HttpServletRequest request, HttpServletResponse response, Authentication authentication)
          throws IOException {
    // 기존 리다이렉트 동작 제거
    clearAuthenticationAttributes(request);

    // JSON 응답
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");

    // 사용자 이메일 가져오기
    String email = authentication.getName();
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    // JWT 토큰 생성
    String accessToken = jwtUtil.createAccessToken(user.getEmail(), "ROLE_USER", user.getFamily().getFamilyId());
    String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), user.getFamily().getFamilyId());

    response.getWriter().write(String.format(
            "{\"accessToken\": \"%s\", \"refreshToken\": \"%s\"}", accessToken, refreshToken));
  }
}
