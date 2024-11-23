package com.danpoong.withu.letter.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class LettersByLikeDateResponse {
    private LocalDate date;
    private List<LetterByLikeResponse> letters;
    private int count;
}
