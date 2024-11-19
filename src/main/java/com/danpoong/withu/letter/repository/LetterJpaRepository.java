package com.danpoong.withu.letter.repository;

import com.danpoong.withu.letter.domain.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LetterJpaRepository extends JpaRepository<Letter, Long> {
}
