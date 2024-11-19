//package com.danpoong.withu.letter.service;
//
//import com.danpoong.withu.letter.controller.response.LetterResponse;
//import com.danpoong.withu.letter.dto.LetterReqDto;
//import com.danpoong.withu.letter.dto.TextReqDto;
//
//import java.util.Map;
//
//public interface LetterService {
//    Map<String, String> getSignUrl (Long memberId, String fileName);
//    LetterResponse saveFile(Long memberId, Long recordId, LetterReqDto request);
//    Map<String, String> getDownloadUrl(Long memberId, String keyName);
//    LetterResponse deleteFile(Long memberId, String fileName);
//    LetterResponse saveText(Long memberId, Long recordId, TextReqDto urlReqDto);
//    LetterResponse deleteText(Long memberId, TextReqDto urlReqDto);
//}
