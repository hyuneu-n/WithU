package com.danpoong.withu.letter.service;

import com.danpoong.withu.common.exception.ResourceNotFoundException;
import com.danpoong.withu.letter.controller.response.LetterResponse;
import com.danpoong.withu.letter.domain.Letter;
import com.danpoong.withu.letter.dto.LetterReqDto;
import com.danpoong.withu.letter.repository.LetterRepository;
import com.danpoong.withu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LetterServiceImpl implements LetterService{

    private final LetterRepository letterRepository;
    private final S3Presigner s3Presigner;

    private final UserRepository userRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public Map<String, String> generatePresignedUrl(Long familyId, Long senderId, Long receiverId) {

        if (!userRepository.existsByIdAndFamily_FamilyId(senderId, familyId)) {
            throw new IllegalArgumentException("Sender ID " + senderId + " is not a member of Family ID " + familyId);
        }
        if (!userRepository.existsByIdAndFamily_FamilyId(receiverId, familyId)) {
            throw new IllegalArgumentException("Receiver ID " + receiverId + " is not a member of Family ID " + familyId);
        }

        String keyName = "letters/" + familyId + "/" + senderId + "-" + receiverId + "-" + UUID.randomUUID();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(keyName)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) // URL 유효 시간 10분
                .putObjectRequest(objectRequest)
                .build();

        String presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString();

        Map<String, String> response = new HashMap<>();
        response.put("presignedUrl", presignedUrl);
        response.put("keyName", keyName);
        return response;
    }


    @Override
    @Transactional
    public LetterResponse saveLetter(LetterReqDto request) {

        if (!userRepository.existsByIdAndFamily_FamilyId(request.getSenderId(), request.getFamilyId())) {
            throw new IllegalArgumentException("Sender ID " + request.getSenderId() +
                    " is not a member of Family ID " + request.getFamilyId());
        }
        if (!userRepository.existsByIdAndFamily_FamilyId(request.getReceiverId(), request.getFamilyId())) {
            throw new IllegalArgumentException("Receiver ID " + request.getReceiverId() +
                    " is not a member of Family ID " + request.getFamilyId());
        }

        Letter letter = Letter.builder()
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .familyId(request.getFamilyId())
                .scheduleId(request.getScheduleId())
                .letterType(request.getLetterType())
                .keyName(request.getKeyName())
                .textContent(request.getTextContent())
                .isSaved(true)
                .isLiked(false)
                .build();

        return new LetterResponse(letterRepository.save(letter));
    }

    @Override
    @Transactional
    public List<LetterResponse> getSavedAllLetters(Long receiverId) {
        List<Letter> letters = letterRepository.findAllByReceiverIdAndIsSaved(receiverId, true); // isSaved = true

        return letters.stream()
                .sorted(Comparator
                        .comparing(Letter::getCreatedDate)
                        .thenComparing((l1, l2) -> l2.getId().compareTo(l1.getId()))
                )
                .map(LetterResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<LetterResponse> getNullSavedLetters(Long receiverId) {
        List<Letter> letters = letterRepository.findAllByReceiverIdAndIsSavedIsNull(receiverId);
        return letters.stream()
                .map(LetterResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LetterResponse deleteLetter(Long letterId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new ResourceNotFoundException("Letter", letterId));

        LetterResponse deletedLetterResponse = new LetterResponse(letter);
        letterRepository.delete(letter);

        return deletedLetterResponse;
    }


    @Override
    @Transactional
    public Map<String, String> getDownloadUrl(Long letterId) {
        Letter letter = letterRepository.findById(letterId)
                .orElseThrow(() -> new ResourceNotFoundException("Letter", letterId));

        String keyName = letter.getKeyName();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(builder -> builder.bucket(bucketName).key(keyName).build())
                .build();

        String presignedUrl = s3Presigner.presignGetObject(presignRequest).url().toString();

        // 결과 반환
        Map<String, String> response = new HashMap<>();
        response.put("presignedUrl", presignedUrl);
        response.put("keyName", keyName);
        return response;
    }
}
