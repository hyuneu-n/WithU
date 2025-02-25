package com.danpoong.withu.user.service;

import java.time.LocalDate;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.danpoong.withu.letter.repository.LetterRepository;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.dto.UserRegisterRequest;
import com.danpoong.withu.user.dto.UserResponse;
import com.danpoong.withu.user.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final LetterRepository letterRepository;

  // 첫 로그인 확인
  public Boolean isFirstLogin(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    return user.getRefreshToken() == null; // Refresh Token이 없는 경우 첫 로그인
  }

  // 사용자 정보 등록
  public String registerUserInfo(String email, UserRegisterRequest request) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

    if (user.getRefreshToken() != null) {
      return "사용자의 정보가 이미 등록되었습니다.";
    }

    // 사용자 정보 업데이트
    user.setNickname(request.getNickname());
    user.setBirthday(request.getBirthday());
    user.setProfileImage(request.getProfileImage());
    userRepository.save(user);

    return "사용자 정보가 정상적으로 등록되었습니다.";
  }

  // 사용자 권한 조회
  public String getUserRole(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    return user.getRole();
  }

  // 사용자 정보 조회
  public UserResponse getUserInfo(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    return new UserResponse(
        user.getEmail(),
        user.getNickname(),
        user.getProfileImage(),
        user.getBirthday(),
        user.getRole(),
        user.getFamily() != null ? user.getFamily().getFamilyId() : null);
  }

  // 가족 ID 조회
  public Long getFamilyId(String email) {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    if (user.getFamily() == null) {
      return null; // 가족 그룹이 없는 경우 null 반환
    }
    return user.getFamily().getFamilyId(); // 가족 ID 반환
  }

  // 사용자 ID로 조회
  public User findById(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
  }

  // 이메일로 사용자 조회
  public User findByEmail(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
  }

  // 닉네임 업데이트
  public void updateNickname(Long userId, String newNickname) {
    User user = findById(userId);
    user.setNickname(newNickname);
    userRepository.save(user);
  }

  // 생년월일 업데이트
  public void updateBirthdate(Long userId, LocalDate birthdate) {
    User user = findById(userId);
    user.setBirthday(birthdate);
    userRepository.save(user);
  }

  // 받은 편지 수 조회
  @Transactional(readOnly = true)
  public int getReceivedLettersCount(Long userId) {
    return letterRepository.countByReceiverId(userId);
  }

  // 보낸 편지 수 조회
  @Transactional(readOnly = true)
  public int getSentLettersCount(Long userId) {
    return letterRepository.countBySenderId(userId);
  }

  // 로그아웃
  public void logout(Long userId) {
    User user = findById(userId);
    user.setRefreshToken(null);
    userRepository.save(user);
  }

  // 계정 탈퇴
  public void deleteAccount(Long userId) {
    userRepository.deleteById(userId);
  }

  public List<User> findUsersByFamilyId(Long familyId) {
    return userRepository.findAllByFamily_FamilyId(familyId);
  }

  public List<User> findAllUsers() {
    return userRepository.findAll();
  }

  public List<User> findAllUsersWithNotificationSettings() {
    return userRepository.findAllByPushNotificationTimeIsNotNull();
  }

  public void invalidateRefreshToken(String email) {
    User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    user.setRefreshToken(null);
    userRepository.save(user);
  }

  public void updateRefreshToken(String email, String refreshToken) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

    user.setRefreshToken(refreshToken);
    userRepository.save(user);
    log.info("Refresh Token 업데이트 완료 - 이메일: {}, 새로운 Refresh Token: {}", email, refreshToken);
  }

  public User createUser(String email, String nickname, String profileImage) {
    User newUser = new User();
    newUser.setEmail(email);
    newUser.setNickname(nickname != null ? nickname : "defaultNickname");
    newUser.setProfileImage(profileImage); // 프로필 이미지 저장
    newUser.setRole("ROLE_USER");
    userRepository.save(newUser);
    return newUser;
  }

  public void updateProfileImage(String email, String profileImage) {
    User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    user.setProfileImage(profileImage);
    userRepository.save(user);
    log.info("프로필 이미지 업데이트 - 이메일: {}, 이미지 URL: {}", email, profileImage);
  }
}
