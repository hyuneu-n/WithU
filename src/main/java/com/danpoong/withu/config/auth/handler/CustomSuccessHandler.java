package com.danpoong.withu.config.auth.handler;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Value("${front-url}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String nickname = (String) attributes.getOrDefault("nickname", "defaultNickname");

        log.info("Extracted Email: {}", email);
        log.info("Extracted Nickname: {}", nickname);

        if (email == null || email.isEmpty()) {
            log.error("Email is null or empty. Cannot generate JWT.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid authentication data");
            return;
        }

        // 액세스 및 리프레시 토큰 생성
        String accessToken = jwtUtil.createAccessToken(email, "ROLE_USER");
        String refreshToken = jwtUtil.createRefreshToken(email);
        log.info("Generated Access Token: {}", accessToken);
        log.info("Generated Refresh Token: {}", refreshToken);

        // 쿠키에 저장
        response.addCookie(createJwtCookie("Authorization", accessToken, 3600)); // 1시간
        response.addCookie(createJwtCookie("Refresh-Token", refreshToken, 7 * 24 * 3600)); // 7일
        log.info("Added Tokens to Cookies for email: {}", email);

        // 응답 본문 반환
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{ \"redirectUrl\": \"" + frontUrl + "/home\", \"accessToken\": \"" + accessToken + "\", \"refreshToken\": \"" + refreshToken + "\" }");

        // 리다이렉트 설정
        getRedirectStrategy().sendRedirect(request, response, frontUrl + "/home");
    }

    private Cookie createJwtCookie(String name, String token, int maxAge) {
        Cookie cookie = new Cookie(name, token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        return cookie;
    }
}
