package com.permitseoul.permitserver.domain.admin.guest.core.domain;

import com.permitseoul.permitserver.domain.admin.guest.core.domain.entity.GuestEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Guest {
    private final Long guestId;
    private final String name;
    private final String guestType;
    private final String affiliation;
    private final String phoneNumber;
    private final String email;

    public static Guest fromEntity(final GuestEntity guestEntity) {
        return new Guest(
                guestEntity.getGuestId(),
                guestEntity.getName(),
                guestEntity.getGuestType(),
                guestEntity.getAffiliation(),
                guestEntity.getPhoneNumber(),
                guestEntity.getEmail()
        );
    }
}
