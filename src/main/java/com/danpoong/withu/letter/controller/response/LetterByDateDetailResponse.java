package com.danpoong.withu.letter.controller.response;

import com.danpoong.withu.letter.domain.LetterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class LetterByDateDetailResponse {
    private Long letterId;
    private LetterType letterType;
    private String senderNickname;
    private String textContent;
    private Boolean isLiked;
    private LocalDateTime createdAt;
}
