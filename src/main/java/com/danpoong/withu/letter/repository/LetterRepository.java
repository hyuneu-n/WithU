package com.danpoong.withu.letter.repository;

import com.danpoong.withu.letter.domain.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Long> {
    List<Letter> findAllByReceiverId(Long receiverId);
    List<Letter> findAllByReceiverIdAndIsSaved(Long receiverId, Boolean isSaved);
    List<Letter> findAllByReceiverIdAndIsSavedIsNull(Long receiverId);

    int countByReceiverId(Long receiverId);
    int countBySenderId(Long senderId);

    List<Letter> findAllByReceiverIdAndIsSavedAndCreatedAtBetween(
            Long receiverId, Boolean isSaved, LocalDateTime startDate, LocalDateTime endDate);

}
