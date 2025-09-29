package com.permitseoul.permitserver.domain.admin.ticket.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.admin.ticket.api.dto.res.TicketRoundAndTypeDetailRes;
import com.permitseoul.permitserver.domain.admin.ticketround.core.AdminTicketRoundRetriever;
import com.permitseoul.permitserver.domain.admin.ticketround.exception.AdminTicketRoundNotFoundException;
import com.permitseoul.permitserver.domain.admin.tickettype.core.component.AdminTicketTypeRetriever;
import com.permitseoul.permitserver.domain.ticketround.core.domain.TicketRound;
import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.util.DateFormatterUtil;
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

        final TicketRound ticketRound;
        try {
            ticketRound = adminTicketRoundRetriever.getTicketRoundById(ticketRoundId);
        } catch (AdminTicketRoundNotFoundException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_TICKET_ROUND);
        }

        final List<TicketType> ticketTypes = adminTicketTypeRetriever.getTicketTypesByTicketRoundId(ticketRound.getTicketRoundId());
        final List<TicketRoundAndTypeDetailRes.TicketTypeInfo> ticketTypeInfos = parseTicketTypeInfos(ticketTypes);

        return TicketRoundAndTypeDetailRes.of(
                ticketRound.getTicketRoundId(),
                ticketRound.getTicketRoundTitle(),
                DateFormatterUtil.formatyyyyMMdd(ticketRound.getSalesStartAt()),
                DateFormatterUtil.formatHHmm(ticketRound.getSalesStartAt()),
                DateFormatterUtil.formatyyyyMMdd(ticketRound.getSalesEndAt()),
                DateFormatterUtil.formatHHmm(ticketRound.getSalesEndAt()),
                ticketTypeInfos
        );
    }

    private List<TicketRoundAndTypeDetailRes.TicketTypeInfo> parseTicketTypeInfos(final List<TicketType> ticketTypes) {
        if(ticketTypes.isEmpty()) {
            return List.of();
        }
        return ticketTypes.stream()
                .map(ticketType -> TicketRoundAndTypeDetailRes.TicketTypeInfo.of(
                                ticketType.getTicketTypeId(),
                                ticketType.getTicketTypeName(),
                                ticketType.getTicketPrice(),
                                ticketType.getTotalTicketCount(),
                                DateFormatterUtil.formatyyyyMMdd(ticketType.getTicketStartAt()),
                                DateFormatterUtil.formatHHmm(ticketType.getTicketStartAt()),
                                DateFormatterUtil.formatyyyyMMdd(ticketType.getTicketEndAt()),
                                DateFormatterUtil.formatHHmm(ticketType.getTicketEndAt())
                        )
                )
                .toList();
    }
}
