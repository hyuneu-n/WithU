package com.danpoong.withu.randommessage.repository;

import com.danpoong.withu.randommessage.domain.RandomMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RandomMessageRepository extends JpaRepository<RandomMessage, Long> {
    boolean existsByMessage(String message);
}
