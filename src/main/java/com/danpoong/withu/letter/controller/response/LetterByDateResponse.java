package com.danpoong.withu.letter.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class LetterByDateResponse {
    private LocalDate date;
    private List<LetterByMonthResponse> letters;
    private int count;

}
