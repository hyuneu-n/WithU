package com.danpoong.withu.letter.controller;

import com.danpoong.withu.config.auth.jwt.JwtUtil;
import com.danpoong.withu.letter.controller.response.LetterResponse;
import com.danpoong.withu.letter.controller.response.ScheduleLetterResponse;
import com.danpoong.withu.letter.controller.response.LetterDatailResponse;
import com.danpoong.withu.letter.dto.LetterReqDto;
import com.danpoong.withu.letter.dto.ScheduleLetterRequestDto;
import com.danpoong.withu.letter.dto.TextLetterRequestDto;
import com.danpoong.withu.letter.service.LetterService;
import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.service.UserService;
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
    private final JwtUtil jwtUtil;
    private final UserService userService;

    private Long extractUserId(String bearerToken) {
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization 헤더에 올바른 토큰이 없습니다.");
        }

        String token = bearerToken.substring(7);
        String email = jwtUtil.extractEmail(token);

        if (email == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        // 이메일로 사용자 조회 및 ID 반환
        User user = userService.findByEmail(email);
        return user.getId();
    }

    @GetMapping("/url")
    @Operation(summary = "편지 저장용 Presigned Url 생성", description = "S3 업로드를 위한 Presigned Url을 생성합니다.")
    public ResponseEntity<Map<String, String>> generatePresignedUrl(
            @RequestHeader("Authorization") String bearerToken,
            @RequestParam Long familyId, @RequestParam Long receiverId) {
        Map<String, String> response = letterService.generatePresignedUrl(familyId, extractUserId(bearerToken), receiverId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/file")
    @Operation(summary = "편지 전송(아직 받은 사람은 확인 x)", description = "보낸 편지의 정보를 전송 및 db에 저장합니다.")
    public ResponseEntity<LetterResponse> saveLetter(@RequestHeader("Authorization") String bearerToken,
                                                     @RequestBody LetterReqDto request) {
        LetterResponse response = letterService.saveLetter(extractUserId(bearerToken), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/text")
    @Operation(summary = "텍스트 편지 전송", description = "S3 접근 없이 텍스트 형태의 편지를 전송 및 db에 저장합니다.")
    public ResponseEntity<LetterResponse> saveTextLetter(@RequestHeader("Authorization") String bearerToken,
                                                         @RequestBody TextLetterRequestDto request) {

        LetterResponse response = letterService.saveTextLetter(extractUserId(bearerToken), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/schedule")
    @Operation(summary = "특정 스케줄에 편지 전송", description = "스케줄에 텍스트 형태의 편지를 전송 및 db에 저장합니다.")
    public ResponseEntity<ScheduleLetterResponse> saveTextLetter(@RequestHeader("Authorization") String bearerToken,
                                                                 @RequestBody ScheduleLetterRequestDto request) {

        ScheduleLetterResponse response = letterService.saveScheduleLetter(extractUserId(bearerToken), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/saved")
    @Operation(summary = "모든 편지 조회", description = "사용자가 보관한 모든 편지를 조회합니다.")
    public ResponseEntity<List<LetterResponse>> getSavedLetters(@RequestHeader("Authorization") String bearerToken) {
        List<LetterResponse> responses = letterService.getSavedAllLetters(extractUserId(bearerToken));
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/unsaved")
    @Operation(summary = "화면에 떠다닐 편지 조회", description = "아직 확인하지 않은 편지를 조회합니다.")
    public ResponseEntity<List<LetterResponse>> getNullSavedLetters(@RequestHeader("Authorization") String bearerToken) {
        List<LetterResponse> responses = letterService.getNullSavedLetters(extractUserId(bearerToken));
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PatchMapping("{letterId}")
    @Operation(summary = "편지 보관", description = "보관하기 버튼을 클릭하면 보관 여부를 true로 변경합니다.")
    public ResponseEntity<LetterResponse> saveLetter(@PathVariable Long letterId) {
        LetterResponse updatedLetter = letterService.updateLetterAsSaved(letterId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedLetter);
    }

    @DeleteMapping("{letterId}")
    @Operation(summary = "편지 삭제", description = "특정 편지를 삭제하고 삭제된 편지 정보를 반환합니다.")
    public ResponseEntity<LetterResponse> deleteLetter(@PathVariable Long letterId) {
        LetterResponse deletedLetter = letterService.deleteLetter(letterId);
        return ResponseEntity.status(HttpStatus.OK).body(deletedLetter);
    }

    @GetMapping("/download/{letterId}")
    @Operation(summary = "편지 조회용 Presigned Url 생성", description = "편지 파일을 다운받기 위한 Presigned URL을 생성합니다.")
    public ResponseEntity<Map<String, String>> getDownloadUrl(@PathVariable Long letterId) {
        Map<String, String> response = letterService.getDownloadUrl(letterId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/like/{letterId}")
    @Operation(summary = "찜 상태 변경", description = "편지의 찜 상태를 현재 상태에 따라 반대로 변경합니다.")
    public ResponseEntity<LetterResponse> changeLetterLike(@PathVariable Long letterId) {
        LetterResponse response = letterService.changeLikeState(letterId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/text/{letterId}")
    @Operation(summary = "편지 세부 내용 조회", description = "편지의 세부 내용을 조회합니다.")
    public ResponseEntity<LetterDatailResponse> getTextContent(@PathVariable Long letterId) {
        LetterDatailResponse textLetterResponse = letterService.getLetterDatail(letterId);
        return ResponseEntity.ok(textLetterResponse);
    }


}
