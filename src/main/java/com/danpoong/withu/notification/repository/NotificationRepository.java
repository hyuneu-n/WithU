package com.danpoong.withu.notification.repository;

import com.danpoong.withu.notification.domain.Notification;
import com.danpoong.withu.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByUser(User user); // 특정 사용자에 대한 알림 조회
}
