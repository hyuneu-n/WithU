package com.danpoong.withu.family.controller;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.family.domain.Family;
import com.danpoong.withu.family.service.FamilyService;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Operation(summary = "초대 링크 생성", description = "가족 초대하는 링크 생성")
    @PostMapping("/invite")
    public ResponseEntity<String> generateInviteLink(
            @RequestHeader("Authorization") String bearerToken) {

        Long userId = extractUserId(bearerToken);
        User user = userService.findById(userId);

        // 사용자가 이미 가족 그룹에 속해 있는지 확인
        Family family = user.getFamily();

        if (family == null) {
            // 가족 그룹이 없으면 새로운 가족 그룹을 생성하고 회원을 추가
            family = familyService.createFamilyForUser(user);
        }

        // 초대 링크 생성 및 반환
        String inviteLink = familyService.createInviteLink(family.getFamilyId(), userId);
        return ResponseEntity.ok(inviteLink);
    }

    @Operation(summary = "초대 링크 처리", description = "초대 링크를 통해 가족에 가입")
    @PostMapping("/join-by-invite")
    public ResponseEntity<String> joinFamilyByInvite(
            @RequestParam String inviteLink,
            @RequestHeader("Authorization") String bearerToken) {

        Long userId = extractUserId(bearerToken);

        // 초대 링크 처리 및 가족 그룹 가입
        String result = familyService.joinFamilyByInvite(inviteLink, userId);
        return ResponseEntity.ok(result);
    }

    private Long extractUserId(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더에 올바른 토큰이 없습니다.");
        }

        String token = bearerToken.substring(7);
        String email = jwtUtil.extractEmail(token); // JwtUtil로 이메일 추출

        if (email == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // 이메일을 통해 User를 조회하여 userId 추출
        User user = userService.findByEmail(email);
        return user.getId(); // `getId` 사용
    }
}
