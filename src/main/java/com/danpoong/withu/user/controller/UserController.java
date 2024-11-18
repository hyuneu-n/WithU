package com.danpoong.withu.user.controller;

import com.danpoong.withu.config.auth.dto.RefreshTokenRequest;
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
@Tag(name = "User")
public class UserController {

    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Operation(summary = "액세스 토큰 재발급 (RequestParam)", description = "Refresh Token을 사용하여 새로운 Access Token을 발급")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAuthTokenByRequestParam(@RequestParam String refreshToken, @RequestParam String email) {
        log.debug("Refreshing token for email: {}", email);

        // Refresh Token 유효성 검증
        if (!jwtUtil.validateToken(refreshToken, email)) {
            log.warn("Invalid Refresh Token for email: {}", email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
        }

        // Access Token 생성
        String newAccessToken = jwtUtil.createAccessToken(email, userService.getUserRole(email));
        log.debug("New Access Token created for email: {}", email);

        return ResponseEntity.ok(new AuthResponse(newAccessToken));
    }

    @Operation(summary = "액세스 토큰 재발급 (RequestBody)", description = "Refresh Token을 사용하여 새로운 Access Token을 발급")
    @PostMapping("/refresh-token/body")
    public ResponseEntity<?> refreshAuthTokenByRequestBody(@RequestBody RefreshTokenRequest request) {
        log.debug("Refreshing token for email: {}", request.getEmail());

        // Refresh Token 유효성 검증
        if (!jwtUtil.validateToken(request.getRefreshToken(), request.getEmail())) {
            log.warn("Invalid Refresh Token for email: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
        }

        // Access Token 생성
        String newAccessToken = jwtUtil.createAccessToken(request.getEmail(), request.getNickname());
        log.debug("New Access Token created for email: {}", request.getEmail());

        return ResponseEntity.ok(new AuthResponse(newAccessToken));
    }

    @Operation(summary = "로그아웃", description = "사용자의 인증 쿠키를 삭제")
    @PostMapping("/logout")
    public ResponseEntity<?> userLogOut(HttpServletResponse response) {
        log.debug("Logging out user");
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "[TEST] 사용자 권한 조회", description = "사용자의 권한 정보를 반환")
    @GetMapping("/get-user-role")
    public ResponseEntity<String> getUserRole(@RequestHeader("Authorization") String token) {
        log.debug("Fetching user role");
        String email = jwtUtil.extractEmail(token.substring(7));
        return ResponseEntity.ok(userService.getUserRole(email));
    }

    @Operation(summary = "[TEST] 첫 로그인 여부 확인", description = "사용자가 첫 로그인을 했는지 확인")
    @PostMapping("/check-first-login")
    public ResponseEntity<Boolean> checkFirstLogin(@RequestHeader("Authorization") String token) {
        try {
            log.debug("Checking first login");
            String email = jwtUtil.extractEmail(token.substring(7));
            return ResponseEntity.ok(userService.isFirstLogin(email));
        } catch (Exception e) {
            log.error("Error checking first login: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "사용자 정보 등록", description = "사용자 첫 로그인 후 정보 등록")
    @PostMapping("/register")
    public ResponseEntity<String> registerUserInfo(
            @RequestHeader("Authorization") String token,
            @RequestBody UserRegisterRequest request) {
        try {
            log.debug("Registering user info");
            String email = jwtUtil.extractEmail(token.substring(7));
            String result = userService.registerUserInfo(email, request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error registering user info: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "사용자 정보 조회", description = "사용자의 상세정보 조회")
    @GetMapping("/myInfo")
    public ResponseEntity<UserResponse> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            log.debug("Fetching user info");
            String email = jwtUtil.extractEmail(token.substring(7));
            return ResponseEntity.ok(userService.getUserInfo(email));
        } catch (Exception e) {
            log.error("Error fetching user info: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "[TEST] 홈 화면", description = "로그인 후 리다이렉트 테스트")
    @GetMapping("/home")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("로그인 성공!");
    }
}
