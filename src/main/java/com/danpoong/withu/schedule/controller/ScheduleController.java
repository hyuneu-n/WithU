package com.danpoong.withu.schedule.controller;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.schedule.dto.ScheduleResponseDto;
import com.danpoong.withu.schedule.service.ScheduleService;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
@Tag(name = "Schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final JwtUtil jwtUtil;
    private final UserService userService;


    @PostMapping("/add")
    public ResponseEntity<ScheduleResponseDto> addSchedule(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam String title,
            @RequestParam(required = false) String memo,
            @RequestParam LocalDate date) {

        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("제목은 필수 입력 항목입니다.");
        }

        if (date == null) {
            throw new IllegalArgumentException("날짜는 필수 입력 항목입니다.");
        }

        String token = extractToken(bearerToken); // 토큰 유효성 검사 및 추출
        String email = jwtUtil.extractEmail(token); // JWT에서 이메일 추출
        User user = userService.findByEmail(email);

        String authorName = user.getNickname();
        Long userId = user.getId();
        Long familyId = user.getFamily() != null ? user.getFamily().getFamilyId() : null;

        ScheduleResponseDto scheduleResponse = scheduleService.addSchedule(userId, familyId, title, memo, date, authorName);
        return ResponseEntity.ok(scheduleResponse);
    }


    @Operation(summary = "일정 수정", description = "일정 수정")
    @PutMapping("/update/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> updateSchedule(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long scheduleId,
            @RequestParam String title,
            @RequestParam(required = false) String memo,
            @RequestParam LocalDate date) {

        String email = extractEmailFromToken(bearerToken);
        User user = userService.findByEmail(email);
        Long userId = user.getId();

        ScheduleResponseDto updatedScheduleResponse = scheduleService.updateSchedule(scheduleId, userId, title, memo, date);
        return ResponseEntity.ok(updatedScheduleResponse);
    }

    @Operation(summary = "특정 일정 조회", description = "일정 ID로 특정 일정을 조회")
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponseDto> getScheduleById(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long scheduleId) {

        Long userId = extractUserId(bearerToken); // 토큰에서 userId 추출
        ScheduleResponseDto scheduleDto = scheduleService.findScheduleByUserAndFamily(userId, scheduleId);

        return ResponseEntity.ok(scheduleDto);
    }

    @Operation(summary = "일정 삭제", description = "일정 ID로 특정 일정 삭제")
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<String> deleteSchedule(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long scheduleId) {
        String email = jwtUtil.extractEmail(bearerToken.substring(7));
        User user = userService.findByEmail(email);

        scheduleService.deleteSchedule(scheduleId, user.getId());
        return ResponseEntity.ok("일정이 성공적으로 삭제되었습니다.");
    }

    private String extractEmailFromToken(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더에 올바른 토큰이 없습니다.");
        }
        String token = bearerToken.substring(7);
        return jwtUtil.extractEmail(token);
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

        // 이메일로 사용자 조회 및 ID 반환
        User user = userService.findByEmail(email);
        return user.getId();
    }

    @Operation(summary = "전체 일정 조회", description = "사용자 또는 가족 ID에 해당하는 전체 일정 조회")
    @GetMapping("/all")
    public ResponseEntity<List<ScheduleResponseDto>> getAllSchedules(@RequestHeader("Authorization") String bearerToken) {
        String email = jwtUtil.extractEmail(bearerToken.substring(7));
        User user = userService.findByEmail(email);

        Long userId = user.getId();
        Long familyId = user.getFamily() != null ? user.getFamily().getFamilyId() : null;

        List<ScheduleResponseDto> schedules = scheduleService.findSchedulesByUserOrFamily(userId, familyId);
        return ResponseEntity.ok(schedules);
    }

    private String extractToken(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더에 올바른 토큰이 없습니다.");
        }
        return bearerToken.substring(7).trim();
    }
}
