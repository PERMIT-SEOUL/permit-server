package com.permitseoul.permitserver.domain.admin.ticket.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.admin.ticket.api.dto.res.TicketRoundAndTypeDetailRes;
import com.permitseoul.permitserver.domain.admin.ticketround.core.AdminTicketRoundRetriever;
import com.permitseoul.permitserver.domain.admin.ticketround.exception.AdminTicketRoundNotFoundException;
import com.permitseoul.permitserver.domain.admin.tickettype.core.component.AdminTicketTypeRetriever;
import com.permitseoul.permitserver.domain.ticketround.core.domain.TicketRound;
import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTicketService {
    private final AdminTicketTypeRetriever adminTicketTypeRetriever;
    private final AdminTicketRoundRetriever adminTicketRoundRetriever;

    @Transactional(readOnly = true)
    public TicketRoundAndTypeDetailRes getTicketRoundAndTypeDetails(final long ticketRoundId) {
        try {
            final TicketRound ticketRound = adminTicketRoundRetriever.getTicketRoundById(ticketRoundId);
            final List<TicketType> ticketTypes = adminTicketTypeRetriever.getTicketTypesByTicketRoundId(ticketRound.getTicketRoundId());


        } catch (AdminTicketRoundNotFoundException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_TICKET_ROUND);
        }


    }
}
