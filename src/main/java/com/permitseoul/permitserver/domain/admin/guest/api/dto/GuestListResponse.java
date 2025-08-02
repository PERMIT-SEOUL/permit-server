package com.permitseoul.permitserver.domain.admin.guest.api.dto;


import java.util.List;

public record GuestListResponse(
        List<GuestInfo> guests
) {

    public static GuestListResponse of(final List<GuestInfo> guestInfoList) {
        return new GuestListResponse(guestInfoList);
    }

    public record GuestInfo(
            long id,
            String guestName,
            String guestType,
            String affiliation,
            String phoneNumber,
            String email
    ) {
        public static GuestInfo of(
                final long id,
                final String guestName,
                final String guestType,
                final String affiliation,
                final String phoneNumber,
                final String email) {
            return new GuestInfo(id, guestName, guestType, affiliation, phoneNumber, email);
        }
    }
}
