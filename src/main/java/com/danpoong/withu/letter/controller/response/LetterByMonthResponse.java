package com.danpoong.withu.letter.controller.response;

import com.danpoong.withu.letter.domain.Letter;
import com.danpoong.withu.letter.domain.LetterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Data
@Getter
@Builder
@AllArgsConstructor
public class LetterByMonthResponse {
    private Long letterId;
    private Boolean isLiked;
    private LocalDateTime createdAt;

}
