package com.permitseoul.permitserver.domain.admin.guest.core.component;

import com.permitseoul.permitserver.domain.admin.guest.api.dto.GuestListResponse;
import com.permitseoul.permitserver.domain.admin.guest.core.domain.Guest;
import com.permitseoul.permitserver.domain.admin.guest.core.domain.entity.GuestEntity;
import com.permitseoul.permitserver.domain.admin.guest.core.repository.GuestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminGuestRetriever {
    private final GuestRepository guestRepository;

    public List<Guest> findAllGuestList() {
        final List<GuestEntity> guestEntityList = guestRepository.findAll();
        return guestEntityList.stream()
                .map(Guest::fromEntity)
                .toList();
    }


}
