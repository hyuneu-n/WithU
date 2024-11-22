package com.danpoong.withu.mypage.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyPageResponseDto {
  private String nickname;
  private long daysTogether;
  private int receivedLetters;
  private int sentLetters;
  private LocalDate birthdate;
}
