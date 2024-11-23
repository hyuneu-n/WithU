package com.danpoong.withu.notification.dto;

import com.danpoong.withu.notification.domain.Notification;
import com.danpoong.withu.notification.domain.NotificationType;
import com.danpoong.withu.user.domain.User;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationDto {

    private String msgTitle; // 메시지 제목
    private NotificationType type; // 알림 유형

    @Builder
    public NotificationDto(String msgTitle, NotificationType type) {
        this.msgTitle = msgTitle;
        this.type = type;
    }

    public Notification toEntity(User user) {
        return Notification.builder()
                .user(user)
                .msgTitle(msgTitle)
                .type(type)
                .build();
    }
}
