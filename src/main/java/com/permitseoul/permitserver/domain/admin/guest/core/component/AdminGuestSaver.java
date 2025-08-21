package com.permitseoul.permitserver.domain.admin.guest.core.component;

import com.permitseoul.permitserver.domain.admin.guest.core.domain.entity.GuestEntity;
import com.permitseoul.permitserver.domain.admin.guest.core.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminGuestSaver {
    private final GuestRepository guestRepository;

    @Transactional
    public void saveGuest(final String guestName,
                          final String guestType,
                          final String guestAffiliation,
                          final String guestPhoneNumber,
                          final String guestEmail) {
        guestRepository.save(GuestEntity.create(guestName, guestType, guestAffiliation, guestPhoneNumber, guestEmail));
    }
}
