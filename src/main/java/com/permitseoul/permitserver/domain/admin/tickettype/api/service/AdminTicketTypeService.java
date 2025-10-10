package com.permitseoul.permitserver.domain.admin.tickettype.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.admin.tickettype.core.component.AdminTicketTypeRemover;
import com.permitseoul.permitserver.domain.admin.tickettype.core.component.AdminTicketTypeRetriever;
import com.permitseoul.permitserver.domain.admin.tickettype.core.exception.AdminTicketTypeNotFoundException;
import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminTicketTypeService {
    private final AdminTicketTypeRetriever adminTicketTypeRetriever;
    private final AdminTicketTypeRemover adminTicketTypeRemover;

    @Transactional
    public void deleteTicketType(final long ticketTypeId) {
        try {
            final TicketType ticketType = adminTicketTypeRetriever.getTicketTypeById(ticketTypeId);
            adminTicketTypeRemover.deleteTicketTypeById(ticketType.getTicketTypeId());
        } catch (AdminTicketTypeNotFoundException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        }
    }
}
