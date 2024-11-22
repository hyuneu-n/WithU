package com.danpoong.withu.notification.domain;

import com.danpoong.withu.user.domain.User;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "msg_title", nullable = false)
    private String msgTitle;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Builder
    public Notification(String msgTitle, User user, NotificationType type) {
        this.msgTitle = msgTitle;
        this.user = user;
        this.type = type;
    }
}
