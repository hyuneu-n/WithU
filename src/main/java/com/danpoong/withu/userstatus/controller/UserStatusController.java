package com.danpoong.withu.userstatus.controller;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.family.domain.Family;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.service.UserService;
import com.danpoong.withu.userstatus.domain.UserStatus;
import com.danpoong.withu.userstatus.dto.UserStatusResponseDto;
import com.danpoong.withu.userstatus.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/userStatus")
@RequiredArgsConstructor
@Slf4j
public class UserStatusController {

    private final UserStatusService userStatusService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Operation(summary = "사용자 상태 작성", description = "사용자의 상태 작성")
    @PostMapping("/create")
    public ResponseEntity<UserStatusResponseDto> createStatus(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam(required = false) UserStatus.StatusEmoji emoji,
            @RequestParam(required = false) String text) {

        Long userId = extractUserId(bearerToken);

        // 둘 중 하나만 설정되도록 처리
        if (emoji != null && text != null) {
            text = null; // text가 들어오면 emoji만 유지
        }

        UserStatus userStatus = userStatusService.createOrUpdateStatus(userId, emoji, text);
        return ResponseEntity.ok(new UserStatusResponseDto(userStatus));
    }

    @Operation(summary = "사용자 상태 업데이트", description = "사용자의 상태를 업데이트")
    @PutMapping("/update")
    public ResponseEntity<UserStatusResponseDto> updateStatus(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam(required = false) UserStatus.StatusEmoji emoji,
            @RequestParam(required = false) String text) {

        Long userId = extractUserId(bearerToken);

        // 둘 중 하나만 설정되도록 처리
        if (emoji != null && text != null) {
            text = null; // text가 들어오면 emoji만 유지
        }

        UserStatus updatedStatus = userStatusService.createOrUpdateStatus(userId, emoji, text);
        return ResponseEntity.ok(new UserStatusResponseDto(updatedStatus));
    }

    @Operation(summary = "가족 그룹 멤버들의 상태 조회", description = "가족 그룹에 속한 모든 멤버들의 상태를 조회")
    @GetMapping("/family")
    public ResponseEntity<List<UserStatusResponseDto>> getFamilyMemberStatuses(
            @RequestHeader("Authorization") String bearerToken) {

        try {
            Long userId = extractUserId(bearerToken);
            User user = userService.findById(userId);
            Family family = user.getFamily();

            if (family == null) {
                log.error("가족 구성원이 없습니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of());
            }

            List<UserStatus> familyStatuses = userStatusService.getStatusByFamilyId(family.getFamilyId());

            List<UserStatusResponseDto> responseDtos = familyStatuses.stream()
                    .map(UserStatusResponseDto::new)
                    .toList();

            return ResponseEntity.ok(responseDtos);

        } catch (IllegalArgumentException e) {
            log.error("가족 구성원이 없습니다: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(List.of());
        }
    }

    @Operation(summary = "나의 상태 조회", description = "현재 로그인한 사용자의 상태를 조회")
    @GetMapping("/my-status")
    public ResponseEntity<UserStatusResponseDto> getMyStatus(
            @RequestHeader("Authorization") String bearerToken) {

        Long userId = extractUserId(bearerToken);

        // 사용자 상태 조회
        UserStatus userStatus = userStatusService.getStatusByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자의 상태를 찾을 수 없습니다."));

        return ResponseEntity.ok(new UserStatusResponseDto(userStatus));
    }

    @Operation(summary = "사용자 상태 삭제", description = "사용자 상태 삭제")
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteStatus(
            @RequestHeader("Authorization") String bearerToken) {

        Long userId = extractUserId(bearerToken);
        userStatusService.deleteStatusByUserId(userId);

        return ResponseEntity.ok("상태가 성공적으로 삭제되었습니다.");
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
