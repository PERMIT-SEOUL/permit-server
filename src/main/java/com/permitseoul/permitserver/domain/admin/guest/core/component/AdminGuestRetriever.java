package com.permitseoul.permitserver.domain.admin.guest.core.component;

import com.permitseoul.permitserver.domain.admin.guest.core.domain.Guest;
import com.permitseoul.permitserver.domain.admin.guest.core.domain.entity.GuestEntity;
import com.permitseoul.permitserver.domain.admin.guest.core.exception.AdminGuestNotFoundException;
import com.permitseoul.permitserver.domain.admin.guest.core.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminGuestRetriever {
    private final GuestRepository guestRepository;

    @Transactional(readOnly = true)
    public List<Guest> findAllGuestList() {
        final List<GuestEntity> guestEntityList = guestRepository.findAll();
        return guestEntityList.stream()
                .map(Guest::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Guest findById(final long guestId) {
        final GuestEntity guestEntity = guestRepository.findById(guestId).orElseThrow(AdminGuestNotFoundException::new);
        return Guest.fromEntity(guestEntity);
    }

}
