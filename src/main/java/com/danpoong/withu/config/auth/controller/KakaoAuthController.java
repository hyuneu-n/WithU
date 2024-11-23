package com.danpoong.withu.config.auth.controller;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.config.auth.service.KakaoAuthService;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginWithKakao(@RequestHeader("Authorization") String kakaoAccessToken) {
        try {
            // Bearer 제거
            if (kakaoAccessToken.toLowerCase().startsWith("bearer ")) {
                kakaoAccessToken = kakaoAccessToken.substring(7).trim(); // Bearer 제거
            }

            // 카카오 사용자 정보 가져오기
            Map<String, Object> kakaoUserInfo = kakaoAuthService.getKakaoUserInfo(kakaoAccessToken);

            // 이메일, 닉네임, 프로필 이미지 추출
            String email = kakaoAuthService.extractEmail(kakaoUserInfo);
            String nickname = kakaoAuthService.extractNickname(kakaoUserInfo);
            String profileImage = kakaoAuthService.extractProfileImage(kakaoUserInfo);

            if (email == null || email.isEmpty()) {
                log.error("카카오 사용자 정보에 이메일이 없습니다.");
                return ResponseEntity.badRequest().body(Map.of("error", "카카오 사용자 정보에 이메일이 없습니다."));
            }

            // 사용자 조회 또는 신규 생성
            User user = userService.findByEmail(email);
            if (user == null) {
                log.info("신규 사용자 생성 - 이메일: {}", email);
                user = userService.createUser(email, nickname, profileImage);
            } else {
                // 프로필 사진 업데이트 (선택적으로 필요)
                if (profileImage != null) {
                    userService.updateProfileImage(email, profileImage);
                }
            }

            // 가족 ID 확인 (null 처리)
            Long familyId = user.getFamily() != null ? user.getFamily().getFamilyId() : null;

            // JWT 토큰 생성
            String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getRole(), familyId);
            String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), familyId);

            // 응답 데이터 생성
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("profileImage", profileImage != null ? profileImage : ""); // null 처리

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "로그인 처리 실패"));
        }
    }

}
