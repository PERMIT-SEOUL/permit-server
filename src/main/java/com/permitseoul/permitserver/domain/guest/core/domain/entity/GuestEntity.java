package com.permitseoul.permitserver.domain.guest.core.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "guests")
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class GuestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guest_id")
    private Long guestId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "guest_type", nullable = false)
    private String guestType;

    @Column(name = "affiliation", nullable = false)
    private String affiliation; //소속

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email", nullable = false)
    private String email;
}
