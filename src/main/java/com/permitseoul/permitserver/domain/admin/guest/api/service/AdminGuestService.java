package com.permitseoul.permitserver.domain.admin.guest.api.service;

import com.permitseoul.permitserver.domain.admin.guest.api.dto.GuestListResponse;
import com.permitseoul.permitserver.domain.admin.guest.core.component.AdminGuestRetriever;
import com.permitseoul.permitserver.domain.admin.guest.core.domain.Guest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminGuestService {
    private final AdminGuestRetriever adminGuestRetriever;

    public GuestListResponse getGuestList() {
        final List<Guest> guestList = adminGuestRetriever.findAllGuestList();
        final List<GuestListResponse.GuestInfo> guestInfoList = guestList.stream()
                .map(guest -> {
                    return GuestListResponse.GuestInfo.of(
                            guest.getGuestId(),
                            guest.getName(),
                            guest.getGuestType(),
                            guest.getAffiliation(),
                            guest.getPhoneNumber(),
                            guest.getEmail()
                            );
                }).toList();

        return GuestListResponse.of(guestInfoList);

    }
}
