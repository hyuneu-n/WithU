package com.danpoong.withu.family.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

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
    }
}
