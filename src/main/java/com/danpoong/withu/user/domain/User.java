package com.danpoong.withu.user.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column
    private String profileImage;

    @Column
    private String birthday; // 추후 마이페이지에서 설정 예정 -> 카카오 권한 없음!

    @Column(nullable = false)
    private String role;

    @Column
    private String refreshToken;
}
