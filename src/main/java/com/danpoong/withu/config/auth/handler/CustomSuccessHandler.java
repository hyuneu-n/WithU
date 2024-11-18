package com.danpoong.withu.config.auth.handler;

import java.io.IOException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    private static final String AUTHORIZATION_COOKIE = "Authorization";
    private static final String REFRESH_TOKEN_COOKIE = "Refresh-Token";
    private static final String DEFAULT_ROLE = "ROLE_USER";
    private static final String REDIRECT_URL = "http://localhost:8080/api/users/home";
    private static final int ACCESS_TOKEN_MAX_AGE = 60 * 60; // 1시간
    private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7일

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        // 사용자 정보 추출
        String email = authentication.getName();
        log.info("Authenticated email: {}", email);

        // 역할 가져오기
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(DEFAULT_ROLE);
        log.info("User role: {}", role);

        // 토큰 생성
        String accessToken = jwtUtil.createAccessToken(email, role);
        String refreshToken = jwtUtil.createRefreshToken(email);

        // Refresh 토큰 저장
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // 쿠키 설정 및 추가
        response.addCookie(createCookie(AUTHORIZATION_COOKIE, accessToken, ACCESS_TOKEN_MAX_AGE));
        response.addCookie(createCookie(REFRESH_TOKEN_COOKIE, refreshToken, REFRESH_TOKEN_MAX_AGE));
        log.info("Access and Refresh tokens added to cookies for email: {}", email);

        // 리다이렉트 설정
        response.sendRedirect(REDIRECT_URL);
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true); // 클라이언트에서 스크립트로 접근 못하게 설정
        cookie.setPath("/"); // 모든 경로에서 쿠키 사용 가능
        // cookie.setSecure(true); // HTTPS 환경에서만 사용할 경우 활성화
        return cookie;
    }
}
