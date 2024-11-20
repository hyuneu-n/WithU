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
    @Operation(summary = "Generate S3 Presigned URL", description = "S3 업로드를 위한 Presigned Url을 생성합니다.")
    public ResponseEntity<Map<String, String>> generatePresignedUrl(
            @RequestParam Long familyId, @RequestParam Long senderId, @RequestParam Long receiverId) {
        Map<String, String> response = letterService.generatePresignedUrl(familyId, senderId, receiverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping
    @Operation(summary = "Save letter", description = "보낸 편지의 정보를 저장합니다.")
    public ResponseEntity<LetterResponse> saveLetter(@RequestBody LetterReqDto request) {
        LetterResponse response = letterService.saveLetter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get letters by receiver", description = "받은 사람 기준으로 모든 편지를 조회합니다.")
    public ResponseEntity<List<LetterResponse>> getLettersByReceiver(@RequestParam Long receiverId) {
        List<LetterResponse> responses = letterService.getLettersByReceiver(receiverId);
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/download")
    @Operation(summary = "Get letter download URL", description = "편지 파일을 다운받기 위한 Presigned URL을 생성합니다.")
    public ResponseEntity<Map<String, String>> getDownloadUrl(@RequestParam Long letterId) {
        Map<String, String> response = letterService.getDownloadUrl(letterId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
