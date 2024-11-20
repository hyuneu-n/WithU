package com.danpoong.withu.letter.service;

import com.danpoong.withu.letter.controller.response.LetterResponse;
import com.danpoong.withu.letter.dto.LetterReqDto;

import java.util.List;
import java.util.Map;

public interface LetterService {
    Map<String, String> generatePresignedUrl(Long familyId, Long senderId, Long receiverId);
    LetterResponse saveLetter(LetterReqDto request);
    List<LetterResponse> getSavedAllLetters(Long receiverId);
    List<LetterResponse> getNullSavedLetters(Long receiverId);
    Map<String, String> getDownloadUrl(Long letterId);
    LetterResponse deleteLetter(Long letterId);
}
