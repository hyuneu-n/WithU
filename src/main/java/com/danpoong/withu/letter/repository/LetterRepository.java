package com.danpoong.withu.letter.repository;

import java.util.List;

import com.danpoong.withu.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import com.danpoong.withu.letter.domain.Letter;

public interface LetterRepository extends JpaRepository<Letter, Long> {
//  List<Letter> findAllByReceiverId(Long receiverId);

  List<Letter> findAllByReceiverIdAndIsSaved(Long receiverId, Boolean isSaved);

  List<Letter> findAllByReceiverIdAndIsSavedIsNull(Long receiverId);
//  boolean existsByReceiverIdAndIsReadFalse(User receiver); // 읽었는지 안 읽었는지

  int countByReceiverId(Long receiverId);

  List<Letter> findAllByReceiverIdAndIsSavedAndCreatedAtBetween(
          Long receiverId, Boolean isSaved, LocalDateTime startDate, LocalDateTime endDate);
  List<Letter> findAllByReceiverIdAndIsLikedAndCreatedAtBetween(
          Long receiverId,
          Boolean isLiked,
          LocalDateTime startDateTime,
          LocalDateTime endDateTime
  );

  int countBySenderId(Long senderId);
}
