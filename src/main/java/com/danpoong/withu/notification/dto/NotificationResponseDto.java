package com.danpoong.withu.notification.dto;

import com.danpoong.withu.notification.domain.Notification;

import lombok.Getter;

@Getter
public class NotificationResponseDto {

    private Long id;
    private String msgTitle;
    private String type;

    public NotificationResponseDto(Notification notification) {
        this.id = notification.getId();
        this.msgTitle = notification.getMsgTitle();
        this.type = notification.getType().name();
    }
}
