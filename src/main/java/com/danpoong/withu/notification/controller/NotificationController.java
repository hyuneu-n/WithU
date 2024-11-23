//package com.danpoong.withu.notification.controller;
//
//import com.danpoong.withu.config.auth.jwt.JwtUtil;
//import com.danpoong.withu.notification.service.NotificationService;
//import com.danpoong.withu.user.domain.User;
//import com.danpoong.withu.user.service.UserService;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//@RestController
//@RequestMapping("/api/notification")
//@RequiredArgsConstructor
//@Tag(name = "Notification", description = "알림 관련 API")
//public class NotificationController {
//
//    private final NotificationService notificationService;
//    private final JwtUtil jwtUtil;
//    private final UserService userService;
//
//    @Operation(summary = "SSE 연결 설정", description = "클라이언트와 SSE 연결을 설정")
//    @GetMapping("/connect")
//    public SseEmitter connect(@RequestHeader("Authorization") String bearerToken) {
//        Long userId = extractUserId(bearerToken);
//        return notificationService.connect(userId);
//    }
//
//    @Operation(summary = "알림 전송 테스트", description = "특정 사용자에게 테스트 알림을 전송")
//    @PostMapping("/test")
//    public ResponseEntity<String> testNotification(
//            @RequestHeader("Authorization") String bearerToken,
//            @RequestParam String content) {
//        Long userId = extractUserId(bearerToken);
//        notificationService.sendTestNotification(userId, content);
//        return ResponseEntity.ok("테스트 알림이 전송되었습니다.");
//    }
//
//    private Long extractUserId(String bearerToken) {
//        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
//            throw new IllegalArgumentException("Authorization 헤더에 올바른 토큰이 없습니다.");
//        }
//        String token = bearerToken.substring(7);
//        String email = jwtUtil.extractEmail(token);
//        User user = userService.findByEmail(email);
//        return user.getId();
//    }
//}
