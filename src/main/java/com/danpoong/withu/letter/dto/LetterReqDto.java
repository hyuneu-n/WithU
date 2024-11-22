package com.danpoong.withu.letter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import jakarta.validation.constraints.NotNull;

import com.danpoong.withu.letter.domain.LetterType;

@Getter
@Builder
@AllArgsConstructor
@Data
public class LetterReqDto {

  @NotNull private Long receiverId;
  @NotNull private Long familyId;
  private Long scheduleId;
  @NotNull private LetterType letterType;
  private String keyName;
  private String textContent;
}
