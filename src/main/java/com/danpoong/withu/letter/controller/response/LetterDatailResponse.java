package com.danpoong.withu.letter.controller.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import com.danpoong.withu.letter.domain.LetterType;

@Data
@Getter
@Builder
@AllArgsConstructor
public class LetterDatailResponse {
  private Long letterId;
  private Long senderId;
  private String senderNickName;
  private Long receiverId;
  private String receiverNickName;
  private Long scheduleId;
  private String scheduleName;

  private LetterType letterType;
  private String keyName;
  private String textContent;
  private Boolean isLiked;
  private LocalDateTime createdAt;
}
