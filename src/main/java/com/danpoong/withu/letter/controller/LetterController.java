package com.danpoong.withu.letter.controller;

import com.danpoong.withu.letter.controller.response.LetterResponse;
import com.danpoong.withu.letter.dto.LetterReqDto;
import com.danpoong.withu.letter.service.LetterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Letter", description = "Letter management APIs")
@RequestMapping("/letter")
public class LetterController {

    private final LetterService letterService;

    @GetMapping("/url")
    @Operation(summary = "편지 저장용 Presigned Url 생성", description = "S3 업로드를 위한 Presigned Url을 생성합니다.")
    public ResponseEntity<Map<String, String>> generatePresignedUrl(
            @RequestParam Long familyId, @RequestParam Long senderId, @RequestParam Long receiverId) {
        Map<String, String> response = letterService.generatePresignedUrl(familyId, senderId, receiverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping
    @Operation(summary = "편지 저장", description = "보낸 편지의 정보를 저장합니다.")
    public ResponseEntity<LetterResponse> saveLetter(@RequestBody LetterReqDto request) {
        LetterResponse response = letterService.saveLetter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/saved")
    @Operation(summary = "모든 편지 조회", description = "보관된 모든 편지를 조회합니다.")
    public ResponseEntity<List<LetterResponse>> getSavedLetters(@RequestParam Long receiverId) {
        List<LetterResponse> responses = letterService.getSavedAllLetters(receiverId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/unsaved")
    @Operation(summary = "화면에 떠다닐 편지 조회", description = "아직 확인하지 않은 편지를 조회합니다.")
    public ResponseEntity<List<LetterResponse>> getNullSavedLetters(@RequestParam Long receiverId) {
        List<LetterResponse> responses = letterService.getNullSavedLetters(receiverId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @DeleteMapping
    @Operation(summary = "편지 삭제", description = "특정 편지를 삭제하고 삭제된 편지 정보를 반환합니다.")
    public ResponseEntity<LetterResponse> deleteLetter(@RequestParam Long letterId) {
        LetterResponse deletedLetter = letterService.deleteLetter(letterId);
        return ResponseEntity.status(HttpStatus.OK).body(deletedLetter);
    }

    @GetMapping("/download")
    @Operation(summary = "편지 조회용 Presigned Url 생성", description = "편지 파일을 다운받기 위한 Presigned URL을 생성합니다.")
    public ResponseEntity<Map<String, String>> getDownloadUrl(@RequestParam Long letterId) {
        Map<String, String> response = letterService.getDownloadUrl(letterId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
