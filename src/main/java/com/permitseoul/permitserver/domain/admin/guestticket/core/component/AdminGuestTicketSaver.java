package com.permitseoul.permitserver.domain.admin.guestticket.core.component;

import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity.GuestTicketEntity;
import com.permitseoul.permitserver.domain.admin.guestticket.core.repository.GuestTicketRepository;
import com.permitseoul.permitserver.global.TicketCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminGuestTicketSaver {
    private final GuestTicketRepository guestTicketRepository;

    public List<GuestTicketEntity> createTickets(final long eventId, final long guestId, final int count) {
        final List<GuestTicketEntity> toSave = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            String code = null;
            code = TicketCodeGenerator.generateTicketCode();
            toSave.add(GuestTicketEntity.create(eventId, guestId, code));
        }

        try {
            return guestTicketRepository.saveAll(toSave);
        } catch (DataIntegrityViolationException e) {
            // 극희박: existsBy 통과 후 save에서 유니크 충돌 가능 → 상위 재시도 정책 고려
            throw new IllegalStateException("티켓 저장 중 유니크 제약 위반", e);
        }
    }
}
