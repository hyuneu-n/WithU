package com.danpoong.withu.family.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.danpoong.withu.family.domain.Family;
import com.danpoong.withu.family.repository.FamilyRepository;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class FamilyService {

  private final FamilyRepository familyRepository;
  private final UserRepository userRepository;
  private final InviteTokenService inviteTokenService;

  @Transactional
  public String createInviteLink(Long familyId, Long userId) {
    // 멤버 조회
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: userId=" + userId));

    Family family;
    if (user.getFamily() != null) {
      family = user.getFamily();
      familyId = family.getFamilyId();
    } else {
      Family newFamily = new Family();
      familyRepository.save(newFamily);
      familyId = newFamily.getFamilyId();
      user.setFamily(newFamily);
      userRepository.save(user);
    }

    // InviteTokenService를 통해 초대 토큰 생성
    String inviteToken = inviteTokenService.generateInviteToken(familyId);

    return "http://15.164.29.113:8080/invite?token=" + inviteToken;
  }

  @Transactional
  public String joinFamilyByInvite(String inviteLink, Long userId) {
    Long familyId = validateInviteToken(extractTokenFromLink(inviteLink));

    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: userId=" + userId));

    if (user.getFamily() != null) {
      return "이미 다른 가족 그룹에 속해 있습니다.";
    }

    Family family =
        familyRepository
            .findById(familyId)
            .orElseThrow(() -> new RuntimeException("가족 그룹을 찾을 수 없습니다: familyId=" + familyId));
    user.setFamily(family);
    userRepository.save(user);

    return "가족 그룹에 성공적으로 가입되었습니다.";
  }

  private Long validateInviteToken(String token) {
    return inviteTokenService.validateInviteToken(token); // InviteTokenService의 메서드 사용
  }

  private String extractTokenFromLink(String inviteLink) {
    String tokenPrefix = "token=";
    int tokenStartIndex = inviteLink.indexOf(tokenPrefix);

    if (tokenStartIndex == -1) {
      throw new IllegalArgumentException("초대 링크에 토큰이 포함되어 있지 않습니다.");
    }

    return inviteLink.substring(tokenStartIndex + tokenPrefix.length());
  }

  @Transactional(readOnly = true)
  public Family findById(Long familyId) {
    return familyRepository.findById(familyId)
            .orElseThrow(() -> new IllegalArgumentException("Family not found with ID: " + familyId));
  }

  @Transactional
  public Family createFamilyForUser(User user) {
    Family newFamily = new Family();
    newFamily = familyRepository.save(newFamily); // 가족 그룹 저장

    // 멤버를 생성된 가족 그룹에 포함
    user.setFamily(newFamily);
    userRepository.save(user); // 멤버 업데이트 저장

    return newFamily;
  }

  // 새로운 Family 생성 메서드
  public Family createDefaultFamilyForUser(User user) {
    Family family = new Family();
    family = familyRepository.save(family); // 새로운 Family 저장
    return family;
  }
}
