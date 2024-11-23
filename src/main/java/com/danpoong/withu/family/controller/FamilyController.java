package com.danpoong.withu.family.controller;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.family.domain.Family;
import com.danpoong.withu.family.dto.FamilyMembersResponseDto;
import com.danpoong.withu.family.service.FamilyService;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
@Tag(name = "Family")
public class FamilyController {

  private final FamilyService familyService;
  private final JwtUtil jwtUtil;
  private final UserService userService;

  @Operation(summary = "초대 링크 생성", description = "가족 초대 링크를 생성합니다.")
  @PostMapping("/invite")
  public ResponseEntity<String> generateInviteLink(
          @RequestHeader("Authorization") String bearerToken) {

    Long userId = extractUserId(bearerToken);
    User user = userService.findById(userId);

    // 사용자가 가족 그룹에 속해 있는지 확인
    Family family = user.getFamily();
    if (family == null) {
      return ResponseEntity.badRequest().body("가족 그룹이 설정되어 있지 않습니다.");
    }

    // 초대 링크 생성 및 반환
    String inviteLink = familyService.createInviteLink(family.getFamilyId(), userId);
    return ResponseEntity.ok(inviteLink);
  }

  @Operation(summary = "초대 링크 처리", description = "초대 링크를 통해 가족 그룹에 가입합니다.")
  @PostMapping("/join-by-invite")
  public ResponseEntity<String> joinFamilyByInvite(
          @RequestParam String inviteLink, @RequestHeader("Authorization") String bearerToken) {

    Long userId = extractUserId(bearerToken);
    String result = familyService.joinFamilyByInvite(inviteLink, userId);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "가족 멤버 리스트 조회", description = "본인이 속한 가족 멤버 리스트를 조회합니다.")
  @GetMapping("/members")
  public ResponseEntity<FamilyMembersResponseDto> getFamilyMembers(
          @RequestHeader("Authorization") String bearerToken) {

    Long userId = extractUserId(bearerToken);
    User user = userService.findById(userId);

    // 사용자가 속한 가족 확인
    Family family = user.getFamily();
    if (family == null) {
      return ResponseEntity.ok(new FamilyMembersResponseDto(null, List.of())); // 가족 ID가 null인 응답 반환
    }

    // 가족 멤버 조회
    List<User> familyMembers = userService.findUsersByFamilyId(family.getFamilyId());
    List<FamilyMembersResponseDto.MemberDto> memberDtos =
            familyMembers.stream()
                    .map(member -> new FamilyMembersResponseDto.MemberDto(
                            member.getId(),
                            member.getNickname(),
                            member.getEmail(),
                            member.getProfileImage())) // 프로필 이미지 추가
                    .toList();

    // 응답 생성
    FamilyMembersResponseDto response =
            new FamilyMembersResponseDto(family.getFamilyId(), memberDtos);
    return ResponseEntity.ok(response);
  }

  private Long extractUserId(String bearerToken) {
    if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
      throw new IllegalArgumentException("Authorization 헤더에 올바른 토큰이 없습니다.");
    }
    String token = bearerToken.substring(7);
    String email = jwtUtil.extractEmail(token);
    User user = userService.findByEmail(email);
    return user.getId();
  }
}
