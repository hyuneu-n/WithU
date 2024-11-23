package com.danpoong.withu.randommessage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.danpoong.withu.randommessage.domain.RandomMessage;

@Repository
public interface RandomMessageRepository extends JpaRepository<RandomMessage, Long> {
  boolean existsByMessage(String message);
}
