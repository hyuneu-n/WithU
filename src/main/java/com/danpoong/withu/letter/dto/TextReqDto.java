package com.danpoong.withu.letter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import jakarta.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@Getter
@Data
public class TextReqDto {
  @NotNull private String textTitle;
  @NotNull private String text;
}
