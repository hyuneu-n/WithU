package com.danpoong.withu.config.auth.handler;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String email = authentication.getName(); // Security Context에서 email 추출
        String accessToken = jwtUtil.createAccessToken(email, "ROLE_USER");

        Cookie cookie = new Cookie("Authorization", accessToken);
        cookie.setPath("/");
        cookie.setMaxAge(3600); // 1시간

        response.addCookie(cookie);
        response.sendRedirect("http://localhost:3000");
    }
}
