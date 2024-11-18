package com.danpoong.withu.test;

import jakarta.persistence.*;

@Entity
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "testentity_id")
    private Long id;
}
