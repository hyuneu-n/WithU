package com.danpoong.withu.family.service;

import com.danpoong.withu.family.domain.Family;
import com.danpoong.withu.family.repository.FamilyRepository;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FamilyService {

  private final FamilyRepository familyRepository;
  private final UserRepository userRepository;
  private final InviteTokenService inviteTokenService;

  @Transactional
  public String createInviteLink(Long familyId, Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: userId=" + userId));

    Family family = user.getFamily();
    if (family == null) {
      family = createFamilyForUser(user);
    }

    // 초대 토큰 생성
    String inviteToken = inviteTokenService.generateInviteToken(family.getFamilyId());
    return "http://15.164.29.113:8080/invite?token=" + inviteToken;
  }

  @Transactional
  public String joinFamilyByInvite(String inviteLink, Long userId) {
    Long familyId = validateInviteToken(extractTokenFromLink(inviteLink));

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: userId=" + userId));

    if (user.getFamily() != null) {
      return "이미 다른 가족 그룹에 속해 있습니다.";
    }

    Family family = familyRepository.findById(familyId)
            .orElseThrow(() -> new RuntimeException("가족 그룹을 찾을 수 없습니다: familyId=" + familyId));

    user.setFamily(family);
    userRepository.save(user);

    return "가족 그룹에 성공적으로 가입되었습니다.";
  }

  private Long validateInviteToken(String token) {
    return inviteTokenService.validateInviteToken(token);
  }

  private String extractTokenFromLink(String inviteLink) {
    String tokenPrefix = "token=";
    if (inviteLink == null || !inviteLink.contains(tokenPrefix)) {
      throw new IllegalArgumentException("유효하지 않은 초대 링크입니다.");
    }
    return inviteLink.substring(inviteLink.indexOf(tokenPrefix) + tokenPrefix.length());
  }

  @Transactional(readOnly = true)
  public Family findById(Long familyId) {
    return familyRepository.findById(familyId)
            .orElseThrow(() -> new IllegalArgumentException("Family not found with ID: " + familyId));
  }

  @Transactional
  public Family createFamilyForUser(User user) {
    Family newFamily = new Family();
    newFamily = familyRepository.save(newFamily);

    user.setFamily(newFamily);
    userRepository.save(user);

    return newFamily;
  }
  // 새로운 Family 생성 메서드
  public Family createDefaultFamilyForUser(User user) {
    Family family = new Family();
    family = familyRepository.save(family); // 새로운 Family 저장
    return family;
  }
}
