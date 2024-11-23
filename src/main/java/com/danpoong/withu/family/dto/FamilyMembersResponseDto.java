package com.danpoong.withu.family.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FamilyMembersResponseDto {
  private Long familyId;
  private List<MemberDto> members;

  @Getter
  @AllArgsConstructor
  public static class MemberDto {
    private Long userId;
    private String nickname;
    private String email;
    private String profileImage; // 프로필 이미지 추가
  }
}