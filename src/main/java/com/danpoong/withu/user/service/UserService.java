package com.danpoong.withu.user.service;

import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.dto.UserRegisterRequest;
import com.danpoong.withu.user.dto.UserResponse;
import com.danpoong.withu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 첫 로그인 확인
    public Boolean isFirstLogin(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return user.getRefreshToken() == null; // Refresh Token이 없는 경우 첫 로그인
    }

    // 사용자 정보 등록
    public String registerUserInfo(String email, UserRegisterRequest request) {
        User user = userRepository.findByEmail(email)
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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return user.getRole();
    }

    // 사용자 정보 조회
    public UserResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return new UserResponse(
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage(),
                user.getBirthday(),
                user.getRole(),
                user.getFamily() != null ? user.getFamily().getFamilyId() : null
        );
    }

    // 가족 ID 조회
    public Long getFamilyId(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        if (user.getFamily() == null) {
            return null; // 가족 그룹이 없는 경우 null 반환
        }
        return user.getFamily().getFamilyId(); // 가족 ID 반환
    }

    // 사용자 ID로 조회
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
    }

    // 이메일로 사용자 조회
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
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

//    // 받은 편지 수 조회
//    public int getReceivedLettersCount(Long userId) {
//        return letterRepository.countByRecipientId(userId);
//    }

//    // 보낸 편지 수 조회
//    public int getSentLettersCount(Long userId) {
//        return letterRepository.countBySenderId(userId);
//    }

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
}
