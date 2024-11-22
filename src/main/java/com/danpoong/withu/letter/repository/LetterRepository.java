package com.danpoong.withu.letter.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.danpoong.withu.letter.domain.Letter;

public interface LetterRepository extends JpaRepository<Letter, Long> {
  List<Letter> findAllByReceiverId(Long receiverId);

  List<Letter> findAllByReceiverIdAndIsSaved(Long receiverId, Boolean isSaved);

  List<Letter> findAllByReceiverIdAndIsSavedIsNull(Long receiverId);

  int countByReceiverId(Long receiverId);

  int countBySenderId(Long senderId);
}
