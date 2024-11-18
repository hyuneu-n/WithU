package com.danpoong.withu.config.auth.handler;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

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

        // 이메일
        String email = authentication.getName();
        log.info("Authenticated email: {}", email);

        // 역할
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(DEFAULT_ROLE);
        log.info("User role: {}", role);

        // 액세스&리프레시 토큰 생성
        String accessToken = jwtUtil.createAccessToken(email, role);
        String refreshToken = jwtUtil.createRefreshToken(email);

        // Refresh 토큰 저장
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // 토큰 쿠키에 추가
        response.addCookie(createCookie(AUTHORIZATION_COOKIE, accessToken, ACCESS_TOKEN_MAX_AGE));
        response.addCookie(createCookie(REFRESH_TOKEN_COOKIE, refreshToken, REFRESH_TOKEN_MAX_AGE));
        log.info("Access and Refresh tokens added to cookies for email: {}", email);

        // 리다이렉트 설정
        String redirectUrl = frontUrl + redirectUrlSuffix;
        response.sendRedirect(redirectUrl);
    }

    /**
     * JWT를 쿠키로 생성하는 메서드
     *
     * @param name   쿠키 이름
     * @param value  쿠키 값
     * @param maxAge 쿠키 유효 시간 (초)
     * @return 생성된 쿠키 객체
     */
    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true); // 클라이언트 스크립트 접근 불가
        cookie.setPath("/"); // 모든 경로에서 사용 가능
        return cookie;
    }
}
