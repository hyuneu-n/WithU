package com.danpoong.withu.userstatus.dto;

import com.danpoong.withu.userstatus.domain.UserStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusResponseDto {
    private Long userId;
    private String name;
    private Long familyId;
    private String statusEmoji;
    private String statusText;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime expiresAt;

    public UserStatusResponseDto(UserStatus userStatus) {
        this.userId = userStatus.getUser().getId();
        this.name = userStatus.getUser().getNickname();
        this.familyId = userStatus.getUser().getFamily() != null ? userStatus.getUser().getFamily().getFamilyId() : null;
        this.statusEmoji = userStatus.getStatusEmoji() != null ? userStatus.getStatusEmoji().name() : null;
        this.statusText = userStatus.getStatusText();
        this.expiresAt = userStatus.getExpiresAt();
    }
}
