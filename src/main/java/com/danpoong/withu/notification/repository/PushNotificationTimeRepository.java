package com.danpoong.withu.notification.repository;

import com.danpoong.withu.notification.domain.PushNotificationTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PushNotificationTimeRepository extends JpaRepository<PushNotificationTime, Long> {
    Optional<PushNotificationTime> findByUserId(Long userId);
}