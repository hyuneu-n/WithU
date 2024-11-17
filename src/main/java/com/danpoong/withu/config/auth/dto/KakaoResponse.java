package com.danpoong.withu.config.auth.dto;

import java.util.Map;

public class KakaoResponse {

    private final Map<String, Object> attribute;
    private final Map<String, Object> kakaoAccountAttribute;
    private final Map<String, Object> profileAttribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
        this.kakaoAccountAttribute = (Map<String, Object>) attribute.get("kakao_account");
        this.profileAttribute = (Map<String, Object>) kakaoAccountAttribute.get("profile");
    }

    public String getProviderId() {
        return attribute.get("id").toString();
    }

    public String getEmail() {
        return kakaoAccountAttribute.get("email").toString();
    }

    public String getNickname() {
        return profileAttribute.get("nickname").toString();
    }

    public String getProfileImage() {
        return profileAttribute.get("profile_image_url").toString();
    }
}
