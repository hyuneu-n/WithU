package com.danpoong.withu.user.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegisterRequest {
    private String nickname;
    private String profileImage;
    private LocalDate birthday;
}
