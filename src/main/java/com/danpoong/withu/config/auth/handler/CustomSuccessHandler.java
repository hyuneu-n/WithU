package com.danpoong.withu.config.auth.handler;

import com.danpoong.withu.config.auth.dto.CustomOAuth2User;
import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String nickname = (String) attributes.get("nickname");

        log.info("추출된 Email: {}", email);
        log.info("추출된 Nickname: {}", nickname);

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
            // **도메인 구매 시 수정 필요
            response.getWriter().write("{ \"redirectUrl\": \"http://localhost:8080/api/users/home\", \"accessToken\": \"" + accessToken + "\" }");

            // 리다이렉트 설정
            // **도메인 구매 시 수정 필요
            getRedirectStrategy().sendRedirect(request, response, "http://localhost:8080/api/users/home");
        } else {
            log.error("Email is null. JWT 생성 불가");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid authentication data");
        }
    }
}
