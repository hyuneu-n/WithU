package com.danpoong.withu.letter.domain;

import com.danpoong.withu.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Letter extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "letter_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long familyId;

    @Column(nullable = false)
    private Long scheduleId;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    private String audioUrl;

    private String imageUrl;

    private String keyName;

    private String textContent;

    @Column(nullable = false)
    private Boolean isSaved;

    @Column(nullable = false)
    private Boolean isLiked;
}
