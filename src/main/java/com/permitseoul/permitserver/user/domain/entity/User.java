package com.permitseoul.permitserver.user.domain.entity;


import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import com.permitseoul.permitserver.user.domain.Sex;
import com.permitseoul.permitserver.user.domain.SocialType;
import com.permitseoul.permitserver.user.domain.UserRole;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sex sex;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String email;

    @Column(name = "social_id", nullable = false)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Column(name = "user_type", nullable = false)
    private UserRole userType;
}

