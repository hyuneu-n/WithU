//package com.danpoong.withu.letter.dto;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Getter;
//
//@Getter
//@Builder
//@AllArgsConstructor
//public class LetterReqDto {
//    @NotBlank(message = "첨부파일 제목은 필수 입력 항목입니다. 최대 20자 까지 입력 가능")
//    @Size(max = 20)
//    @Schema(description = "첨부파일 제목", example = "이력서 최종", type="string")
//    private String title;
//
//    @NotBlank(message = "필수 입력 항목입니다.")
//    @Schema(description = "파일 keyName",type="string")
//    private String keyName;
//}
