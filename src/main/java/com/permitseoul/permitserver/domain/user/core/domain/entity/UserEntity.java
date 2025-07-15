package com.permitseoul.permitserver.domain.user.core.domain.entity;


import com.permitseoul.permitserver.global.domain.BaseTimeEntity;
import com.permitseoul.permitserver.domain.user.core.domain.Gender;
import com.permitseoul.permitserver.domain.user.core.domain.SocialType;
import com.permitseoul.permitserver.domain.user.core.domain.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String email;

    @Column(name = "social_id", nullable = false)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserRole userType;

    private UserEntity(String name, Gender gender, int age, String email, String socialId, SocialType socialType, UserRole userType) {
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.email = email;
        this.socialId = socialId;
        this.socialType = socialType;
        this.userType = userType;
    }

    public static UserEntity create(final String name,
                                    final Gender gender,
                                    final int age,
                                    final String email,
                                    final String socialId,
                                    final SocialType socialType,
                                    final UserRole userType) {
        return new UserEntity(name, gender, age, email, socialId, socialType, userType);
    }
}

