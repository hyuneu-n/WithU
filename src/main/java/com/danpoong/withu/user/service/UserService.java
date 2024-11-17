package com.danpoong.withu.user.service;

import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.dto.UserRegisterRequest;
import com.danpoong.withu.user.dto.UserResponse;
import com.danpoong.withu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 첫 로그인 확인
    public Boolean isFirstLogin(String email) {
        userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return userRepository.findByEmail(email).get().getRefreshToken() == null;
    }

    // 사용자 정보 등록
    public String registerUserInfo(String email, UserRegisterRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRefreshToken() != null) {
            return "사용자의 정보가 이미 등록되었습니다.";
        }

        user.setNickname(request.getNickname());
        user.setBirthday(request.getBirthday());
        user.setProfileImage(request.getProfileImage());
        userRepository.save(user);

        return "사용자 정보가 정상적으로 등록되었습니다.";
    }

    // 사용자 권한 조회
    public String getUserRole(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getRole();
    }

    // 사용자 정보 조회
    public UserResponse getUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserResponse(
                user.getEmail(),
                user.getNickname(),
                user.getProfileImage(),
                user.getBirthday(),
                user.getRole()
        );
    }
}
