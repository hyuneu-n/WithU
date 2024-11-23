package com.danpoong.withu.letter.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class LetterByLikeResponse {
    private Long letterId;
    private Long senderId;
    private String senderNickName;
    private Boolean isLiked;
    private LocalDateTime createdAt;
}
