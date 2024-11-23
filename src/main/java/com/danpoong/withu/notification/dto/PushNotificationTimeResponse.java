package com.danpoong.withu.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PushNotificationTimeResponse {

    private String amPm; // 오전/오후 구분
    private Integer hour; // 시간
    private Integer minute; // 분
}
