package com.danpoong.withu.config.auth.handler;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // OAuth2User에서 이메일과 닉네임 추출
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = null;
        String nickname = null;

        if (attributes.containsKey("kakao_account")) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");
        }

        if (attributes.containsKey("properties")) {
            Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
            nickname = (String) properties.get("nickname");
        }

        log.info("Authentication successful. Extracted email: {}", email);
        log.info("Authentication successful. Extracted nickname: {}", nickname);

        // 이메일 기반으로 JWT 생성
        if (email != null) {
            String accessToken = jwtUtil.createAccessToken(email, "ROLE_USER");

            log.info("Generated JWT access token for email: {}", email);

            // 쿠키에 JWT 추가
            Cookie cookie = new Cookie("Authorization", accessToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(3600); // 1시간
            response.addCookie(cookie);

            log.info("Added JWT to cookie for email: {}", email);

            // 응답 본문에 리다이렉트 URL과 JWT 반환
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{ \"redirectUrl\": \"http://localhost:3000/home\", \"accessToken\": \"" + accessToken + "\" }");

            // 리다이렉트 설정
            getRedirectStrategy().sendRedirect(request, response, "http://localhost:8080/home");
        } else {
            log.error("Email is null. Cannot generate JWT.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid authentication data");
        }
    }
}
