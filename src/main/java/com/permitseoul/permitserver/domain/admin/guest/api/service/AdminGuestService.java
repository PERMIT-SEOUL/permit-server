package com.permitseoul.permitserver.domain.admin.guest.api.service;

import com.permitseoul.permitserver.domain.admin.guest.api.dto.GuestListResponse;
import com.permitseoul.permitserver.domain.admin.guest.core.component.AdminGuestRetriever;
import com.permitseoul.permitserver.domain.admin.guest.core.component.AdminGuestSaver;
import com.permitseoul.permitserver.domain.admin.guest.core.domain.Guest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AdminGuestService {
    private final AdminGuestRetriever adminGuestRetriever;
    private final AdminGuestSaver adminGuestSaver;

    @Transactional(readOnly = true)
    public GuestListResponse getGuestList() {
        final List<Guest> guestList = adminGuestRetriever.findAllGuestList();
        final List<GuestListResponse.GuestInfo> guestInfoList = guestList.stream()
                .map(guest -> GuestListResponse.GuestInfo.of(
                        guest.getGuestId(),
                        guest.getName(),
                        guest.getGuestType(),
                        guest.getAffiliation(),
                        guest.getPhoneNumber(),
                        guest.getEmail()
                        )).toList();

        return GuestListResponse.of(guestInfoList);
    }

    public void addGuest(final String guestName,
                         final String guestType,
                         final String guestAffiliation,
                         final String guestPhoneNumber,
                         final String guestEmail) {
        adminGuestSaver.saveGuest(guestName, guestType, guestAffiliation, guestPhoneNumber, guestEmail);

    }
}
