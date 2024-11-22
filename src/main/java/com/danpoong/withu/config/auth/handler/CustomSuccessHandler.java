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

  private static final String AUTHORIZATION_COOKIE = "Authorization";
  private static final String REFRESH_TOKEN_COOKIE = "Refresh-Token";
  private static final String DEFAULT_ROLE = "ROLE_USER";
  private static final int ACCESS_TOKEN_MAX_AGE = 60 * 60; // 1시간
  private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7일

  @Override
  public void onAuthenticationSuccess(
          HttpServletRequest request, HttpServletResponse response, Authentication authentication)
          throws IOException {

    // 사용자 이메일 가져오기
    String email = authentication.getName();
    log.info("Authenticated email: {}", email);

    // 역할 및 유저 정보 가져오기
    String role =
            authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .orElse(DEFAULT_ROLE);
    User user =
            userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

    // JWT 토큰 생성
    Long familyId = user.getFamily() != null ? user.getFamily().getFamilyId() : null;
    String accessToken = jwtUtil.createAccessToken(email, role, familyId);
    String refreshToken = jwtUtil.createRefreshToken(email, familyId);

    // Refresh Token 저장
    user.setRefreshToken(refreshToken);
    userRepository.save(user);

    // JSON으로 응답
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(String.format(
            "{\"accessToken\": \"%s\", \"refreshToken\": \"%s\"}", accessToken, refreshToken));
  }
}
