package com.danpoong.withu.config.auth.service;

import com.danpoong.withu.config.dto.KakaoResponse;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        KakaoResponse kakaoResponse = new KakaoResponse(super.loadUser(userRequest).getAttributes());
        String email = kakaoResponse.getEmail();

        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setNickname(kakaoResponse.getNickname());
                    newUser.setProfileImage(kakaoResponse.getProfileImage());
                    newUser.setRole("ROLE_USER");
                    return userRepository.save(newUser);
                });
    }
}
