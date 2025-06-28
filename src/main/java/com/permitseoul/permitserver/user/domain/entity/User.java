package com.permitseoul.permitserver.user.domain.entity;


import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import com.permitseoul.permitserver.user.domain.Sex;
import com.permitseoul.permitserver.user.domain.SocialType;
import com.permitseoul.permitserver.user.domain.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Builder(access =  AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access =  AccessLevel.PRIVATE)
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
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

    public static User create(final String name,
                              final Sex sex,
                              final int age,
                              final String email,
                              final String socialId,
                              final SocialType socialType,
                              final UserRole userType) {
        return User.builder()
                .name(name)
                .sex(sex)
                .age(age)
                .email(email)
                .socialId(socialId)
                .socialType(socialType)
                .userType(userType)
                .build();
    }
}

