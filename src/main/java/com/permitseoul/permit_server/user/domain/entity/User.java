package com.permitseoul.permit_server.user.domain.entity;


import com.permitseoul.permit_server.global.domain.BaseTimeEntity;
import com.permitseoul.permit_server.user.domain.Sex;
import com.permitseoul.permit_server.user.domain.SocialType;
import jakarta.persistence.*;
import org.hibernate.usertype.UserType;

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

    @Column(name = "user_type")
    private UserType userType;
}

