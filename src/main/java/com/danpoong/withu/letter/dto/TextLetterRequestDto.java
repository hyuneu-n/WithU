package com.danpoong.withu.letter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextLetterRequestDto {
    private Long familyId;
    private Long receiverId;
    private String textContent;
}
