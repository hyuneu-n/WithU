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
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginWithKakao(@RequestHeader("Authorization") String kakaoAccessToken) {
        try {
            // Bearer 제거
            if (kakaoAccessToken.toLowerCase().startsWith("bearer ")) {
                kakaoAccessToken = kakaoAccessToken.substring(7).trim();
            }

            // 카카오 사용자 정보 가져오기
            Map<String, Object> kakaoUserInfo = kakaoAuthService.getKakaoUserInfo(kakaoAccessToken);
            log.info("카카오 사용자 정보: {}", kakaoUserInfo);

            // 이메일, 닉네임, 프로필 이미지 추출
            String email = kakaoAuthService.extractEmail(kakaoUserInfo);
            String nickname = kakaoAuthService.extractNickname(kakaoUserInfo);
            String profileImage = kakaoAuthService.extractProfileImage(kakaoUserInfo);

            // 이메일 검증
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
                updateUserProfileImage(user, profileImage); // 프로필 이미지 업데이트
            }

            // 가족 ID 처리 (null 허용)
            Long familyId = Optional.ofNullable(user.getFamily())
                    .map(family -> family.getFamilyId())
                    .orElse(null);

            // JWT 토큰 생성
            String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getRole(), familyId);
            String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), familyId);

            // 응답 데이터 생성
            Map<String, Object> response = new HashMap<>();
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("profileImage", Optional.ofNullable(profileImage).orElse("")); // null 처리
            response.put("familyId", familyId); // 가족 ID 추가

            log.info("로그인 성공: 이메일={}, accessToken 생성 완료", email);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of("error", "로그인 처리 실패"));
        }
    }

    /**
     * 사용자 프로필 이미지를 업데이트합니다.
     */
    private void updateUserProfileImage(User user, String profileImage) {
        if (profileImage != null && !profileImage.isEmpty()) {
            userService.updateProfileImage(user.getEmail(), profileImage);
            log.info("프로필 이미지 업데이트: userId={}, profileImage={}", user.getId(), profileImage);
        }
    }
}
