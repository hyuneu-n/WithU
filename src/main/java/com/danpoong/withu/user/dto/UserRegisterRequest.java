package com.danpoong.withu.user.dto;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String nickname;
    private String profileImage;
    private String birthday;
}
