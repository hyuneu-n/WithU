package com.danpoong.withu.config.auth.service;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String kakaoTokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String kakaoUserInfoUri;

    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;

    public String getAuthorizationUrl() {
        String url = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + kakaoClientId
                + "&redirect_uri=" + kakaoRedirectUri
                + "&response_type=code";
        log.info("생성된 카카오 인가 URL: {}", url);
        return url;
    }

    public String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        log.info("카카오 액세스 토큰 요청 params={}", params);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(kakaoTokenUri, request, Map.class);
            log.info("카카오 액세스 토큰 응답: {}", response.getBody());
            return (String) response.getBody().get("access_token");
        } catch (HttpClientErrorException e) {
            log.error("카카오 액세스 토큰 요청 실패: {}", e.getResponseBodyAsString());
            throw new RuntimeException("카카오 액세스 토큰 요청 실패", e);
        }
    }

    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(kakaoUserInfoUri, HttpMethod.GET, request, Map.class);

        log.info("카카오 사용자 정보 응답: {}", response.getBody());
        return response.getBody();
    }

    public String extractEmail(Map<String, Object> kakaoUserInfo) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) kakaoUserInfo.get("kakao_account");
        return (String) kakaoAccount.get("email");
    }

    public String extractNickname(Map<String, Object> kakaoUserInfo) {
        Map<String, Object> properties = (Map<String, Object>) kakaoUserInfo.get("properties");
        return (String) properties.get("nickname");
    }

    public Long extractFamilyId(Map<String, Object> kakaoUserInfo) {
        return kakaoUserInfo.containsKey("familyId") ? Long.valueOf(kakaoUserInfo.get("familyId").toString()) : null;
    }

    public Map<String, String> generateJwtTokens(String accessToken) {
        // 카카오 액세스 토큰으로 사용자 정보 가져오기
        Map<String, Object> kakaoUserInfo = getKakaoUserInfo(accessToken);

        // 사용자 정보에서 필요한 값 추출 (예: 이메일, 역할)
        String email = extractEmail(kakaoUserInfo);
        String role = "ROLE_USER"; // 기본 역할
        Long familyId = extractFamilyId(kakaoUserInfo); // 예시: 가족 ID 추출

        // JWT 토큰 생성
        String jwtAccessToken = jwtUtil.createAccessToken(email, role, familyId);
        String jwtRefreshToken = jwtUtil.createRefreshToken(email, familyId);

        // 결과 반환
        return Map.of(
                "accessToken", jwtAccessToken,
                "refreshToken", jwtRefreshToken
        );
    }

}
