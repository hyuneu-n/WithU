package com.danpoong.withu.notification.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.notification.dto.PushNotificationTimeRequest;
import com.danpoong.withu.notification.dto.PushNotificationTimeResponse;
import com.danpoong.withu.notification.service.PushNotificationTimeService;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
@Tag(name = "Notification")
public class PushNotificationTimeController {

    private final PushNotificationTimeService pushNotificationTimeService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "푸시 알림 시간 설정", description = "사용자의 푸시 알림 시간을 설정")
    @PutMapping("/time")
    public ResponseEntity<PushNotificationTimeResponse> updateNotificationTime(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody PushNotificationTimeRequest request) {
        Long userId = extractUserId(bearerToken);
        PushNotificationTimeResponse response =
                pushNotificationTimeService.updateNotificationTime(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "푸시 알림 시간 조회", description = "사용자의 푸시 알림 시간을 조회")
    @GetMapping("/time")
    public ResponseEntity<PushNotificationTimeResponse> getNotificationTime(
            @RequestHeader("Authorization") String bearerToken) {
        Long userId = extractUserId(bearerToken);
        PushNotificationTimeResponse response = pushNotificationTimeService.getNotificationTime(userId);
        return ResponseEntity.ok(response);
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
