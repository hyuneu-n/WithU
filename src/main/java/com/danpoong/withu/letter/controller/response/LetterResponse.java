//package com.danpoong.withu.letter.controller.response;
//
//import com.danpoong.withu.letter.domain.Letter;
//import com.danpoong.withu.letter.domain.LetterType;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.Getter;
//
//@Data
//@Getter
//@Builder
//@AllArgsConstructor
//public class LetterResponse {
//    private Long letterId;
//    private LetterType letterType;
//    private String letterTitle;
//    private String keyName;
//    private String urlTitle;
//    private String url;
//
//    public LetterResponse(Letter letter){
//        this.letterId = letter.getId();
//        this.letterType = letter.getLetterType();
//        this.letterTitle = letter.getLetterTitle();
//        this.keyName = letter.getKeyName();
//        this.urlTitle = letter.getTextTitle();
//        this.url = letter.getText();
//    }
//}
