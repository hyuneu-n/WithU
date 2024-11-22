package com.danpoong.withu.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.danpoong.withu.config.auth.dto.AuthResponse;
import com.danpoong.withu.config.auth.dto.RefreshTokenRequest;
import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.user.dto.UserRegisterRequest;
import com.danpoong.withu.user.dto.UserResponse;
import com.danpoong.withu.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Collections;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User")
public class UserController {

  private final JwtUtil jwtUtil;
  private final UserService userService;

  @Operation(
          summary = "액세스 토큰 재발급 (RequestParam)",
          description = "Refresh Token을 사용하여 새로운 Access Token을 발급")
  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshAuthTokenByRequestParam(
          @RequestParam String refreshToken, @RequestParam String email) {
    log.info("액세스 토큰 재발급 요청 (RequestParam 방식)");
    log.debug("이메일: {}, 리프레시 토큰: {}", email, refreshToken);

    if (!jwtUtil.validateToken(refreshToken, email)) {
      log.warn("리프레시 토큰이 유효하지 않음 - 이메일: {}", email);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰");
    }

    String newAccessToken = jwtUtil.createAccessToken(
            email, userService.getUserRole(email), userService.getFamilyId(email));
    String newRefreshToken = jwtUtil.createRefreshToken(email, userService.getFamilyId(email));

    // UserService를 통해 Refresh Token 업데이트
    userService.updateRefreshToken(email, newRefreshToken);

    log.info("새로운 액세스 및 리프레시 토큰 생성 완료 - 이메일: {}", email);
    return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
  }

  @Operation(
          summary = "액세스 토큰 재발급 (RequestBody)",
          description = "Refresh Token을 사용하여 새로운 Access Token을 발급")
  @PostMapping("/refresh-token/body")
  public ResponseEntity<?> refreshAuthTokenByRequestBody(@RequestBody RefreshTokenRequest request) {
    log.info("액세스 토큰 재발급 요청 (RequestBody 방식)");
    log.debug("요청 데이터: {}", request);

    if (!jwtUtil.validateToken(request.getRefreshToken(), request.getEmail())) {
      log.warn("리프레시 토큰이 유효하지 않음 - 이메일: {}", request.getEmail());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 리프레시 토큰");
    }

    String newAccessToken = jwtUtil.createAccessToken(
            request.getEmail(),
            userService.getUserRole(request.getEmail()),
            userService.getFamilyId(request.getEmail()));
    String newRefreshToken = jwtUtil.createRefreshToken(request.getEmail(), userService.getFamilyId(request.getEmail()));

    log.info("새로운 액세스 및 리프레시 토큰 생성 완료 - 이메일: {}", request.getEmail());
    return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> userLogOut(@RequestHeader("Authorization") String token) {
    log.info("로그아웃 요청 처리 중");
    try {
      String email = jwtUtil.extractEmail(token.substring(7));
      userService.invalidateRefreshToken(email); // 데이터베이스에서 Refresh Token 삭제
      log.info("사용자 로그아웃 완료 - 이메일: {}", email);
      return ResponseEntity.ok("로그아웃 완료");
    } catch (Exception e) {
      log.error("로그아웃 처리 중 오류 발생: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("로그아웃 처리 중 오류 발생");
    }
  }

  @Operation(summary = "[TEST] 사용자 권한 조회", description = "사용자의 권한 정보를 반환")
  @GetMapping("/get-user-role")
  public ResponseEntity<String> getUserRole(@RequestHeader("Authorization") String token) {
    log.info("사용자 권한 조회 요청 처리 중");
    try {
      String email = jwtUtil.extractEmail(token.substring(7));
      log.debug("토큰에서 추출한 이메일: {}", email);
      String role = userService.getUserRole(email);
      log.info("사용자 권한 조회 완료 - 이메일: {}, 권한: {}", email, role);
      return ResponseEntity.ok(role);
    } catch (Exception e) {
      log.error("사용자 권한 조회 중 오류 발생: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PostMapping("/check-first-login")
  public ResponseEntity<?> checkFirstLogin(@RequestHeader("Authorization") String token) {
    log.info("첫 로그인 여부 확인 요청 처리 중");
    try {
      String email = jwtUtil.extractEmail(token.substring(7));
      log.debug("토큰에서 추출한 이메일: {}", email);
      boolean isFirstLogin = userService.isFirstLogin(email);

      log.info("첫 로그인 여부 확인 완료 - 이메일: {}, 첫 로그인 여부: {}", email, isFirstLogin);
      return ResponseEntity.ok(Collections.singletonMap("isFirstLogin", isFirstLogin));
    } catch (Exception e) {
      log.error("첫 로그인 여부 확인 중 오류 발생: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Collections.singletonMap("error", "오류 발생: " + e.getMessage()));
    }
  }

  @Operation(summary = "사용자 정보 등록", description = "사용자 첫 로그인 후 정보 등록")
  @PostMapping("/register")
  public ResponseEntity<String> registerUserInfo(
          @RequestHeader("Authorization") String token, @RequestBody UserRegisterRequest request) {
    log.info("사용자 정보 등록 요청 처리 중");
    log.debug("요청 데이터: {}", request);

    try {
      String email = jwtUtil.extractEmail(token.substring(7));
      log.debug("토큰에서 추출한 이메일: {}", email);
      String result = userService.registerUserInfo(email, request);
      log.info("사용자 정보 등록 완료 - 이메일: {}", email);
      return ResponseEntity.ok(result);
    } catch (Exception e) {
      log.error("사용자 정보 등록 중 오류 발생: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("오류 발생: " + e.getMessage());
    }
  }

  @Operation(summary = "사용자 정보 조회", description = "사용자의 상세정보 조회")
  @GetMapping("/myInfo")
  public ResponseEntity<UserResponse> getUserInfo(@RequestHeader("Authorization") String token) {
    log.info("사용자 정보 조회 요청 처리 중");
    try {
      String email = jwtUtil.extractEmail(token.substring(7));
      log.debug("토큰에서 추출한 이메일: {}", email);
      UserResponse userInfo = userService.getUserInfo(email);
      log.info("사용자 정보 조회 완료 - 이메일: {}", email);
      return ResponseEntity.ok(userInfo);
    } catch (Exception e) {
      log.error("사용자 정보 조회 중 오류 발생: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
  }

  @Operation(summary = "홈 화면", description = "로그인 성공 후 홈 화면으로 이동")
  @GetMapping("/home")
  public ResponseEntity<String> home() {
    log.info("홈 화면 요청 처리 중");
    return ResponseEntity.ok("로그인 성공!");
  }
}
