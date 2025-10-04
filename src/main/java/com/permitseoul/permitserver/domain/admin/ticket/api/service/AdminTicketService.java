package com.permitseoul.permitserver.domain.admin.ticket.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.admin.ticket.api.dto.res.TicketRoundAndTicketTypeRes;
import com.permitseoul.permitserver.domain.admin.ticket.api.dto.res.TicketRoundAndTypeDetailRes;
import com.permitseoul.permitserver.domain.admin.ticket.core.component.AdminTicketRetriever;
import com.permitseoul.permitserver.domain.admin.ticketround.core.AdminTicketRoundRetriever;
import com.permitseoul.permitserver.domain.admin.ticketround.exception.AdminTicketRoundNotFoundException;
import com.permitseoul.permitserver.domain.admin.tickettype.core.component.AdminTicketTypeRetriever;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticket.core.repository.TicketRepository;
import com.permitseoul.permitserver.domain.ticketround.core.domain.TicketRound;
import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.util.DateFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTicketService {
    private final AdminTicketTypeRetriever adminTicketTypeRetriever;
    private final AdminTicketRoundRetriever adminTicketRoundRetriever;
    private final AdminTicketRetriever adminTicketRetriever;

    private static final int EMPTY_TICKET_COUNT_ZERO = 0;
    private static final List<TicketStatus> SOLD_STATUSES = List.of(TicketStatus.RESERVED, TicketStatus.USED);

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

    @Transactional(readOnly = true)
    public TicketRoundAndTicketTypeRes getTicketRoundWithTicketType(final long eventId) {
        final List<TicketRound> ticketRounds = adminTicketRoundRetriever.getTicketRoundByEventId(eventId);
        if (ticketRounds.isEmpty()) {
            return TicketRoundAndTicketTypeRes.of(EMPTY_TICKET_COUNT_ZERO, EMPTY_TICKET_COUNT_ZERO, BigDecimal.ZERO, List.of());
        }

        final List<TicketRoundAndTicketTypeRes.TicketRoundWithTypes> roundsWithTypes = ticketRounds.stream()
                .sorted(Comparator.comparing(TicketRound::getTicketRoundId))
                .map(this::parseRoundWithTypes)
                .toList();

        // 전체 합계
        final long totalTicketCount = roundsWithTypes.stream()
                .flatMap(r -> r.ticketTypes().stream())
                .mapToLong(TicketRoundAndTicketTypeRes.TicketRoundWithTypes.TicketTypeInfo::ticketTypeTotalCount)
                .sum();

        final long totalSoldCount = roundsWithTypes.stream()
                .flatMap(r -> r.ticketTypes().stream())
                .mapToLong(TicketRoundAndTicketTypeRes.TicketRoundWithTypes.TicketTypeInfo::ticketTypeSoldCount)
                .sum();

        final long totalSoldAmount = roundsWithTypes.stream()
                .flatMap(r -> r.ticketTypes().stream())
                .mapToLong(TicketRoundAndTicketTypeRes.TicketRoundWithTypes.TicketTypeInfo::ticketTypeSoldAmount)
                .sum();

        return TicketRoundAndTicketTypeRes.of(
                totalTicketCount,
                totalSoldCount,
                BigDecimal.valueOf(totalSoldAmount),
                roundsWithTypes
        );
    }

    private TicketRoundAndTicketTypeRes.TicketRoundWithTypes parseRoundWithTypes(final TicketRound round) {
        final List<TicketType> ticketTypes = adminTicketTypeRetriever.getTicketTypesByTicketRoundId(round.getTicketRoundId());

        final List<TicketRoundAndTicketTypeRes.TicketRoundWithTypes.TicketTypeInfo> ticketTypeInfos =
                ticketTypes.stream()
                        .sorted(Comparator.comparing(TicketType::getTicketTypeId))
                        .map(this::parseTicketTypeInfo)
                        .toList();

        return TicketRoundAndTicketTypeRes.TicketRoundWithTypes.of(
                round.getTicketRoundId(),
                round.getTicketRoundTitle(),
                DateFormatterUtil.formatyyyyMMdd(round.getSalesStartAt()),
                DateFormatterUtil.formatHHmm(round.getSalesStartAt()),
                DateFormatterUtil.formatyyyyMMdd(round.getSalesEndAt()),
                DateFormatterUtil.formatHHmm(round.getSalesEndAt()),
                ticketTypeInfos
        );
    }

    private TicketRoundAndTicketTypeRes.TicketRoundWithTypes.TicketTypeInfo parseTicketTypeInfo(final TicketType type) {

        final long soldCount = adminTicketRetriever.getSoldCount(type.getTicketTypeId(), SOLD_STATUSES);
        final BigDecimal soldAmount = adminTicketRetriever.getSoldAmount(type.getTicketTypeId(), SOLD_STATUSES);
        final long refundCount = adminTicketRetriever.getCountByStatus(type.getTicketTypeId(), TicketStatus.CANCELED);
        final long usedCount = adminTicketRetriever.getCountByStatus(type.getTicketTypeId(), TicketStatus.USED);

        return TicketRoundAndTicketTypeRes.TicketRoundWithTypes.TicketTypeInfo.of(
                type.getTicketTypeId(),
                type.getTicketTypeName(),
                type.getTicketPrice(),          // 정가
                soldCount,
                type.getTotalTicketCount(),
                soldAmount,                     // 합계 금액
                refundCount,
                usedCount
        );
    }

    private List<TicketRoundAndTypeDetailRes.TicketTypeInfo> parseTicketTypeInfos(final List<TicketType> ticketTypes) {
        if(ticketTypes.isEmpty()) {
            return List.of();
        }
        return ticketTypes.stream()
                .sorted(Comparator.comparing(TicketType::getTicketTypeId))
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
