package com.danpoong.withu.user.controller;

import com.danpoong.withu.user.service.UserService;
import com.danpoong.withu.config.auth.dto.AuthResponse;
import com.danpoong.withu.config.auth.jwt.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAuthToken(@RequestParam String refreshToken, @RequestParam String email) {
        if (!jwtUtil.validateToken(refreshToken, email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
        }
        String newAccessToken = jwtUtil.createAccessToken(email, userService.getUserRole(email));
        return ResponseEntity.ok(new AuthResponse(newAccessToken));
    }

    @PostMapping("/log-out")
    public ResponseEntity<?> userLogOut(HttpServletResponse response) {
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-user-role")
    public ResponseEntity<String> getUserRole(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        return ResponseEntity.ok(userService.getUserRole(email));
    }
}
