package com.permitseoul.permitserver.domain.admin.guest.core.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "guests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    private GuestEntity(String name, String guestType, String affiliation, String phoneNumber, String email) {
        this.name = name;
        this.guestType = guestType;
        this.affiliation = affiliation;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}
