package com.danpoong.withu.notification.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class PushNotificationTimeRequest {

    @NotNull(message = "오전/오후를 선택해주세요.")
    private String amPm; // "오전" 또는 "오후"

    @NotNull(message = "시간을 입력해주세요.")
    @Min(value = 1, message = "시간은 1 이상이어야 합니다.")
    @Max(value = 12, message = "시간은 12 이하이어야 합니다.")
    private Integer hour;

    @NotNull(message = "분을 입력해주세요.")
    @Min(value = 0, message = "분은 0 이상이어야 합니다.")
    @Max(value = 45, message = "분은 45 이하이어야 합니다.")
    private Integer minute;
}
