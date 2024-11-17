package com.danpoong.withu.user.controller;

import com.danpoong.withu.user.dto.UserRegisterRequest;
import com.danpoong.withu.user.dto.UserResponse;
import com.danpoong.withu.user.service.UserService;
import com.danpoong.withu.config.auth.dto.AuthResponse;
import com.danpoong.withu.config.auth.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
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
@Tag(name = "User API", description = "사용자 관련 API")
public class UserController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Operation(summary = "액세스 토큰 재발급", description = "Refresh Token을 사용하여 새로운 Access Token을 발급")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAuthToken(@RequestParam String refreshToken, @RequestParam String email) {
        if (!jwtUtil.validateToken(refreshToken, email)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
        }
        String newAccessToken = jwtUtil.createAccessToken(email, userService.getUserRole(email));
        return ResponseEntity.ok(new AuthResponse(newAccessToken));
    }

    @Operation(summary = "로그아웃", description = "사용자의 인증 쿠키를 삭제")
    @PostMapping("/log-out")
    public ResponseEntity<?> userLogOut(HttpServletResponse response) {
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "사용자 권한 조회", description = "사용자의 권한 정보를 반환")
    @GetMapping("/get-user-role")
    public ResponseEntity<String> getUserRole(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        return ResponseEntity.ok(userService.getUserRole(email));
    }

    @Operation(summary = "첫 로그인 여부 확인", description = "사용자가 첫 로그인을 했는지 확인")
    @PostMapping("/check-first-login")
    public ResponseEntity<Boolean> checkFirstLogin(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            return ResponseEntity.ok(userService.isFirstLogin(email));
        } catch (Exception e) {
            log.error("Error checking first login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "사용자 정보 등록", description = "사용자 첫 로그인 후 정보를 등록")
    @PostMapping("/register")
    public ResponseEntity<String> registerUserInfo(
            @RequestHeader("Authorization") String token,
            @RequestBody UserRegisterRequest request) {
        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            String result = userService.registerUserInfo(email, request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error registering user info: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "사용자 정보 조회", description = "사용자의 상세 정보를 반환")
    @GetMapping("/my-info")
    public ResponseEntity<UserResponse> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.extractEmail(token.substring(7));
            return ResponseEntity.ok(userService.getUserInfo(email));
        } catch (Exception e) {
            log.error("Error fetching user info: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
