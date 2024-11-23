package com.danpoong.withu.config.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoAuthService {

    private final RestTemplate restTemplate;

    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        String userInfoUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            log.info("카카오 사용자 정보 요청: URL={}, Headers={}", userInfoUri, headers);
            ResponseEntity<Map> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, Map.class);
            log.info("카카오 사용자 정보 응답: {}", response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("카카오 사용자 정보 요청 실패: {}", e.getMessage());
            throw new RuntimeException("카카오 사용자 정보 요청 실패", e);
        }
    }

    public String extractEmail(Map<String, Object> kakaoUserInfo) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoUserInfo.get("kakao_account");
        return kakaoAccount != null ? (String) kakaoAccount.get("email") : null;
    }

    public String extractNickname(Map<String, Object> kakaoUserInfo) {
        Map<String, Object> properties = (Map<String, Object>) kakaoUserInfo.get("properties");
        return properties != null ? (String) properties.get("nickname") : null;
    }

    public String extractProfileImage(Map<String, Object> kakaoUserInfo) {
        Map<String, Object> properties = (Map<String, Object>) kakaoUserInfo.get("properties");
        return properties != null ? (String) properties.get("profile_image") : null;
    }
}
