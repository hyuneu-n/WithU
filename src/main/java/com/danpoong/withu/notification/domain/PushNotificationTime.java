package com.danpoong.withu.notification.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushNotificationTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId; // 사용자 ID

    @Column(nullable = false)
    private String amPm; // 오전/오후 구분 ("오전" 또는 "오후")

    @Column(nullable = false)
    private Integer hour; // 시간 (1~12)

    @Column(nullable = false)
    private Integer minute; // 분 (15 단위: 0, 15, 30, 45)

    public void updateTime(String amPm, Integer hour, Integer minute) {
        this.amPm = amPm;
        this.hour = hour;
        this.minute = minute;
    }
}
