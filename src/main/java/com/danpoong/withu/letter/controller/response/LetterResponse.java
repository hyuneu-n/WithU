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
public class LetterResponse {
    private Long letterId;
    private Long senderId;
    private Long receiverId;
    private Long familyId;
    private LetterType letterType;
    private String keyName;
    private String textContent;
    private Boolean isSaved;
    private Boolean isLiked;
    private LocalDateTime createdDate;

    public LetterResponse(Letter letter) {
        this.letterId = letter.getId();
        this.senderId = letter.getSenderId();
        this.receiverId = letter.getReceiverId();
        this.familyId = letter.getFamilyId();
        this.letterType = letter.getLetterType();
        this.keyName = letter.getKeyName();
        this.textContent = letter.getTextContent();
        this.isSaved = letter.getIsSaved();
        this.isLiked = letter.getIsLiked();
        this.createdDate = letter.getCreatedDate();
    }
}
