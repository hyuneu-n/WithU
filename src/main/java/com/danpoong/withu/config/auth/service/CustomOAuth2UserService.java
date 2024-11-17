package com.danpoong.withu.config.auth.service;

import com.danpoong.withu.config.auth.dto.CustomOAuth2User;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 사용자 정보를 데이터베이스에 저장하거나 업데이트
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(oAuth2User));

        // CustomOAuth2User를 생성하고 반환
        return new CustomOAuth2User(user);
    }

    private User createNewUser(OAuth2User oAuth2User) {
        User user = new User();
        user.setEmail(oAuth2User.getAttribute("email"));
        user.setNickname(oAuth2User.getAttribute("nickname"));
        user.setRole("ROLE_USER"); // 기본 역할 설정
        userRepository.save(user);
        return user;
    }
}
