//package com.danpoong.withu.letter.service;
//
//import com.danpoong.withu.common.exception.ResourceNotFoundException;
//import com.danpoong.withu.letter.controller.response.LetterResponse;
//import com.danpoong.withu.letter.domain.Letter;
//import com.danpoong.withu.letter.dto.LetterReqDto;
//import com.danpoong.withu.letter.dto.TextReqDto;
//import com.danpoong.withu.letter.repository.LetterRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.GetObjectRequest;
//import software.amazon.awssdk.services.s3.model.PutObjectRequest;
//import software.amazon.awssdk.services.s3.presigner.S3Presigner;
//import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
//import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
//
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class LetterServiceImpl {
//
//    private final LetterRepository letterRepository;
//    private final S3Presigner s3Presigner;
//    private final S3Client s3Client;
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucketName;
//    @Value("${cloud.aws.s3.bucket-path}")
//    private String bucketPath;
//
//    @Override
//    public Map<String, String> getSignUrl(Long memberId, String fileName) {
//
//        if (letterRepository.existsByMemberIdAndFileTitle(memberId, fileName)) {
//            throw new IllegalArgumentException("이미 존재하는 파일 이름입니다: " + fileName);
//        }
//
//        String keyName = bucketPath + "/" + UUID.randomUUID().toString() + "-" + fileName;
//        PutObjectRequest objectRequest = PutObjectRequest.builder()
//                .bucket(bucketName)
//                .key(keyName)
//                .contentType("application/pdf") // 일단 pdf 파일만 업로드
//                .build();
//        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
//                .signatureDuration(Duration.ofMinutes(10))
//                .putObjectRequest(objectRequest)
//                .build();
//        String presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString();
//
//        Map<String, String> response = new HashMap<>();
//        response.put("signedURL", presignedUrl);
//        response.put("keyName", keyName);
//        return response;
//    }
//
//    @Override
//    @Transactional
//    public LetterResponse saveFile(Long memberId, Long recordId, LetterReqDto request) {
//
//        if (letterRepository.existsByMemberIdAndFileTitle(memberId, request.getTitle())) {
//            throw new IllegalArgumentException("이미 존재하는 파일 이름입니다: " + request.getTitle());
//        }
//
//        Record record = recordRepository.findById(recordId)
//                .orElseThrow(() -> new ResourceNotFoundException("Record", recordId));
//        Letter letter = Letter.builder()
//                .memberId(memberId)
//                .record(record)
//                .fileType(FileType.File)
//                .fileTitle(request.getTitle())
//                .keyName(request.getKeyName())
//                .build();
//        return new LetterResponse(letterRepository.save(letter));
//    }
//
//    @Override
//    public Map<String, String> getDownloadUrl(Long memberId, String fileName) {
//
//        Letter letter = letterRepository.findByMemberIdAndFileTitle(memberId, fileName)
//                .orElseThrow(() -> new IllegalArgumentException("해당 파일을 찾을 수 없습니다: " + fileName));
//        String keyName = letter.getKeyName();
//
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(keyName)
//                .build();
//
//        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//                .signatureDuration(Duration.ofMinutes(10))  // URL 유효 기간 설정
//                .getObjectRequest(getObjectRequest)
//                .build();
//
//        String presignedUrl = s3Presigner.presignGetObject(presignRequest).url().toString();
//
//        Map<String, String> response = new HashMap<>();
//        response.put("presignedURL", presignedUrl);
//        response.put("keyName", keyName);
//        return response;
//    }
//
//    @Override
//    @Transactional
//    public LetterResponse deleteFile(Long memberId, String fileName) {
//        Letter letter = letterRepository.findByMemberIdAndFileTitle(memberId, fileName)
//                .orElseThrow(() -> new IllegalArgumentException("해당 파일이 존재하지 않습니다: " + fileName));
//
//        s3Client.deleteObject(builder -> builder.bucket(bucketName).key(letter.getKeyName()).build());
//        letterRepository.delete(letter);
//
//        return new LetterResponse(letter);
//    }
//
//    @Override
//    @Transactional
//    public LetterResponse saveUrl(Long memberId, Long recordId, TextReqDto urlReqDto) {
//        if (letterRepository.existsByMemberIdAndUrlTitle(memberId, urlReqDto.getTextTitle())) {
//            throw new IllegalArgumentException("이미 존재하는 URL 제목입니다: " + urlReqDto.getTextTitle());
//        }
//        Record record = recordRepository.findById(recordId)
//                .orElseThrow(() -> new ResourceNotFoundException("Record", recordId));
//
//        Letter letter = Letter.builder()
//                .memberId(memberId)
//                .record(record)
//                .fileType(LetterType.Text)
//                .urlTitle(urlReqDto.getTextTitle())
//                .url(urlReqDto.getText())
//                .build();
//        letterRepository.save(letter);
//        return new LetterResponse(letter);
//    }
//
//    @Override
//    @Transactional
//    public LetterResponse deleteUrl(Long memberId, TextReqDto textReqDto) {
//        Letter letter = letterRepository.findByMemberIdAndUrlTitle(memberId, textReqDto.getTextTitle())
//                .orElseThrow(() -> new IllegalArgumentException("해당 URL이 존재하지 않습니다: " + textReqDto.getTextTitle()));
//        letterRepository.delete(letter);
//
//        return new LetterResponse(letter);
//    }
//}
