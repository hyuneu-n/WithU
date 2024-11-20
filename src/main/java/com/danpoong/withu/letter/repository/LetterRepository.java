package com.danpoong.withu.letter.repository;

import com.danpoong.withu.letter.domain.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Long> {
    List<Letter> findAllByReceiverId(Long receiverId);

}
