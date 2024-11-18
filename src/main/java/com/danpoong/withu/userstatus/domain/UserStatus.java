package com.danpoong.withu.userstatus.domain;

import com.danpoong.withu.family.domain.Family;
import com.danpoong.withu.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "user_status")
public class UserStatus {

    public enum StatusEmoji {
        행복, 기쁨, 슬픔, 긴장, 화남, 감동, 놀람
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statusId;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "familyId", nullable = true)
    private Family family;

    @Enumerated(EnumType.STRING)
    private StatusEmoji statusEmoji;

    @Column(length = 255)
    private String statusText;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    public UserStatus(User user, StatusEmoji statusEmoji, String statusText, LocalDateTime expiresAt) {
        this.user = user;
        this.family = user.getFamily();
        this.statusEmoji = statusEmoji;
        this.statusText = statusText;
        this.expiresAt = expiresAt != null ? expiresAt : LocalDateTime.now().plusHours(24); // 만료 시간
    }
}
