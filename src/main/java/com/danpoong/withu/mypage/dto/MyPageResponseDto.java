package com.danpoong.withu.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MyPageResponseDto {
    private String nickname;
    private long daysTogether;
    private int receivedLetters;
    private int sentLetters;
    private LocalDate birthdate;
}