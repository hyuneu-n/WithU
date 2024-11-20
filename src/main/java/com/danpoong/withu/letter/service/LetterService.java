package com.danpoong.withu.letter.service;

import com.danpoong.withu.letter.controller.response.LetterResponse;
import com.danpoong.withu.letter.dto.LetterReqDto;
import com.danpoong.withu.letter.dto.TextLetterRequestDto;

import java.util.List;
import java.util.Map;

public interface LetterService {
    Map<String, String> generatePresignedUrl(Long familyId, Long senderId, Long receiverId);
    LetterResponse saveLetter(Long userId, LetterReqDto request);
    LetterResponse saveTextLetter(Long userId, TextLetterRequestDto request);
    List<LetterResponse> getSavedAllLetters(Long receiverId);
    List<LetterResponse> getNullSavedLetters(Long receiverId);
    Map<String, String> getDownloadUrl(Long letterId);
    LetterResponse deleteLetter(Long letterId);
    LetterResponse updateLetterAsSaved(Long letterId);
    LetterResponse changeLikeState(Long letterId);
}
