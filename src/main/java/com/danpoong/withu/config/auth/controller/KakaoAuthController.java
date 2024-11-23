package com.danpoong.withu.config.auth.controller;

import com.danpoong.withu.config.auth.service.KakaoAuthService;
import com.danpoong.withu.config.auth.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/kakao")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "KakaoAuthController")
public class KakaoAuthController {

    private final KakaoAuthService kakaoAuthService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "카카오 인가코드 요청", description = "카카오 로그인 페이지로 리다이렉트하여 인가코드를 가져옵니다.")
    @GetMapping("/authorize")
    public ResponseEntity<?> getKakaoAuthorizationUrl() {
        String authorizationUrl = kakaoAuthService.getAuthorizationUrl();
        log.info("카카오 인가코드 요청 URL: {}", authorizationUrl);
        return ResponseEntity.ok(Map.of("authorizationUrl", authorizationUrl));
    }

    @Operation(summary = "인가코드를 사용해 JWT 생성", description = "인가코드를 사용해 액세스 토큰 및 리프레시 토큰을 생성합니다.")
    @PostMapping("/token")
    public ResponseEntity<?> exchangeAuthorizationCode(@RequestParam String code) {
        try {
            log.info("받은 인가 코드: {}", code);

            // 기존 사용 여부 확인 (테스트 목적)
            if (code == null || code.isEmpty()) {
                log.error("인가 코드가 전달되지 않았습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "인가 코드가 유효하지 않습니다."));
            }

            // 액세스 토큰 요청
            String kakaoAccessToken = kakaoAuthService.getKakaoAccessToken(code);

            // JWT 토큰 생성
            Map<String, String> jwtTokens = kakaoAuthService.generateJwtTokens(kakaoAccessToken);

            log.info("JWT 토큰 반환: {}", jwtTokens);
            return ResponseEntity.ok(jwtTokens);
        } catch (Exception e) {
            log.error("토큰 변환 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "토큰 변환 실패"));
        }
    }
}
