package com.danpoong.withu.mypage.controller;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.mypage.dto.MyPageResponseDto;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Tag(name = "MyPage")
public class MyPageController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "D+N 조회", description = "함께한 시간 ~")
    @GetMapping("/days-together")
    public ResponseEntity<Long> getDaysTogether(@RequestHeader("Authorization") String bearerToken) {
        Long userId = extractUserId(bearerToken);
        User user = userService.findById(userId);

        // D+N 계산
        long daysTogether = ChronoUnit.DAYS.between(user.getCreatedAt(), LocalDate.now());
        return ResponseEntity.ok(daysTogether);
    }

    // 편지 개발 후 생성 예정
//    @Operation(summary = "받은 편지 수 조회", description = "받은 편지 수")
//    @GetMapping("/letters/received")
//    public ResponseEntity<Integer> getReceivedLetters(@RequestHeader("Authorization") String bearerToken) {
//        Long userId = extractUserId(bearerToken);
//        int receivedLetters = userService.getReceivedLettersCount(userId);
//        return ResponseEntity.ok(receivedLetters);
//    }
//
//    @Operation(summary = "보낸 편지 수 조회", description = "보낸 편지 수")
//    @GetMapping("/letters/sent")
//    public ResponseEntity<Integer> getSentLetters(@RequestHeader("Authorization") String bearerToken) {
//        Long userId = extractUserId(bearerToken);
//        int sentLetters = userService.getSentLettersCount(userId);
//        return ResponseEntity.ok(sentLetters);
//    }

    @Operation(summary = "닉네임 수정", description = "닉네임 수정")
    @PutMapping("/nickname")
    public ResponseEntity<String> updateNickname(@RequestHeader("Authorization") String bearerToken,
                                                 @RequestParam String newNickname) {
        Long userId = extractUserId(bearerToken);
        userService.updateNickname(userId, newNickname);
        return ResponseEntity.ok("닉네임이 변경되었습니다.");
    }

    @Operation(summary = "생년월일 수정", description = "생년월일 수정")
    @PutMapping("/birthdate")
    public ResponseEntity<String> updateBirthdate(@RequestHeader("Authorization") String bearerToken,
                                                  @RequestParam LocalDate birthdate) {
        Long userId = extractUserId(bearerToken);
        userService.updateBirthdate(userId, birthdate);
        return ResponseEntity.ok("생년월일이 변경되었습니다.");
    }

    @Operation(summary = "로그아웃", description = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String bearerToken) {
        Long userId = extractUserId(bearerToken);
        userService.logout(userId);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @Operation(summary = "계정 삭제", description = "탈퇴")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(@RequestHeader("Authorization") String bearerToken) {
        Long userId = extractUserId(bearerToken);
        userService.deleteAccount(userId);
        return ResponseEntity.ok("계정이 탈퇴되었습니다.");
    }

    private Long extractUserId(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더에 올바른 토큰이 없습니다.");
        }
        String token = bearerToken.substring(7);

        String email = jwtUtil.extractEmail(token);
        if (email == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        User user = userService.findByEmail(email);
        return user.getId();
    }
}
