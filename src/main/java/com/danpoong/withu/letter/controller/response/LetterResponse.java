package com.danpoong.withu.letter.controller.response;

import com.danpoong.withu.letter.domain.Letter;
import com.danpoong.withu.letter.domain.LetterType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
@AllArgsConstructor
public class LetterResponse {
    private Long letterId;
    private LetterType letterType;
    private String keyName;
    private String textContent;
    private Boolean isSaved;
    private Boolean isLiked;

    public LetterResponse(Letter letter) {
        this.letterId = letter.getId();
        this.letterType = letter.getLetterType();
        this.keyName = letter.getKeyName();
        this.textContent = letter.getTextContent();
        this.isSaved = letter.getIsSaved();
        this.isLiked = letter.getIsLiked();
    }
}
