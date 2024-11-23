package com.danpoong.withu.notification.service;

import com.danpoong.withu.notification.domain.PushNotificationTime;
import com.danpoong.withu.notification.dto.PushNotificationTimeRequest;
import com.danpoong.withu.notification.dto.PushNotificationTimeResponse;
import com.danpoong.withu.notification.repository.PushNotificationTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PushNotificationTimeService {

    private final PushNotificationTimeRepository pushNotificationTimeRepository;

    @Transactional
    public PushNotificationTimeResponse updateNotificationTime(Long userId, PushNotificationTimeRequest request) {
        PushNotificationTime notificationTime = pushNotificationTimeRepository
                .findByUserId(userId)
                .orElseGet(() -> PushNotificationTime.builder()
                        .userId(userId)
                        .amPm(request.getAmPm())
                        .hour(request.getHour())
                        .minute(request.getMinute())
                        .build());

        notificationTime.updateTime(request.getAmPm(), request.getHour(), request.getMinute());
        pushNotificationTimeRepository.save(notificationTime);

        return new PushNotificationTimeResponse(notificationTime.getAmPm(), notificationTime.getHour(), notificationTime.getMinute());
    }

    @Transactional(readOnly = true)
    public PushNotificationTimeResponse getNotificationTime(Long userId) {
        PushNotificationTime notificationTime = pushNotificationTimeRepository
                .findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("푸시 알림 시간이 설정되지 않았습니다."));
        return new PushNotificationTimeResponse(notificationTime.getAmPm(), notificationTime.getHour(), notificationTime.getMinute());
    }
}
