package com.danpoong.withu.letter.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
@Data
public class TextReqDto {
    @NotNull
    private String textTitle;
    @NotNull
    private String text;
}
