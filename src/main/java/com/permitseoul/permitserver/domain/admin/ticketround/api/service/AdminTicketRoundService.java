package com.permitseoul.permitserver.domain.admin.ticketround.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.admin.ticketround.core.component.AdminTicketRoundRemover;
import com.permitseoul.permitserver.domain.admin.ticketround.core.component.AdminTicketRoundRetriever;
import com.permitseoul.permitserver.domain.admin.ticketround.exception.AdminTicketRoundNotFoundException;
import com.permitseoul.permitserver.domain.admin.tickettype.core.component.AdminTicketTypeRemover;
import com.permitseoul.permitserver.domain.ticketround.core.domain.TicketRound;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminTicketRoundService {
    private final AdminTicketRoundRetriever adminTicketRoundRetriever;
    private final AdminTicketRoundRemover adminTicketRoundRemover;
    private final AdminTicketTypeRemover adminTicketTypeRemover;

    @Transactional
    public void deleteTicketRound(final long ticketRoundId) {
        try {
            final TicketRound ticketRound = adminTicketRoundRetriever.getTicketRoundById(ticketRoundId);
            adminTicketTypeRemover.deleteAllTicketTypeByTicketRoundId(ticketRound.getTicketRoundId());
            adminTicketRoundRemover.deleteTicketRoundById(ticketRound.getTicketRoundId());
        } catch (AdminTicketRoundNotFoundException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_TICKET_ROUND);
        }

    }
}
