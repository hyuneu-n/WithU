package com.danpoong.withu.letter.controller.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import com.danpoong.withu.letter.domain.Letter;
import com.danpoong.withu.letter.domain.LetterType;

@Data
@Getter
@Builder
@AllArgsConstructor
public class ScheduleLetterResponse {
  private Long letterId;
  private Long scheduleId;
  private Long senderId;
  private Long receiverId;
  private LetterType letterType;
  private String keyName;
  private String textContent;
  private Boolean isLiked;
  private LocalDateTime createdAt;

  public ScheduleLetterResponse(Letter letter) {
    this.letterId = letter.getId();
    this.scheduleId = letter.getScheduleId();
    this.senderId = letter.getSenderId();
    this.receiverId = letter.getReceiverId();
    this.letterType = letter.getLetterType();
    this.keyName = letter.getKeyName();
    this.textContent = letter.getTextContent();
    this.isLiked = letter.getIsLiked();
    this.createdAt = letter.getCreatedDate();
  }
}
