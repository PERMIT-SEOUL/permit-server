package com.permitseoul.permitserver.domain.ticket.api.service;

import com.permitseoul.permitserver.domain.ticket.api.dto.EventTicketInfoResponse;
import com.permitseoul.permitserver.domain.ticket.api.exception.NotFoundTicketException;
import com.permitseoul.permitserver.domain.ticketround.core.component.TicketRoundRetriever;
import com.permitseoul.permitserver.domain.ticketround.core.domain.TicketRound;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeRetriever;
import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeNotfoundException;
import com.permitseoul.permitserver.global.exception.PriceFormatException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.util.DateFormatterUtil;
import com.permitseoul.permitserver.global.util.PriceFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TicketService {
    private final TicketRoundRetriever ticketRoundRetriever;
    private final TicketTypeRetriever ticketTypeRetriever;

    @Transactional(readOnly = true)
    public EventTicketInfoResponse getEventTicketInfo(final long eventId, final LocalDateTime now) {
        try {
            // 현재 구매가능한 티켓라운드와 이미 구매 기간 지난 라운드만 조회
            final List<TicketRound> ticketRoundList = ticketRoundRetriever.findSalesOrSalesEndTicketRoundByEventId(eventId, now);
            if (ticketRoundList.isEmpty()) {
                return new EventTicketInfoResponse(List.of());
            }

            final List<Long> ticketRoundIdList = extractTicketRoundIdList(ticketRoundList);

            // 각 티켓 라운드에 해당하는 티켓타입들을 가져와서 Map에 넣음
            final List<TicketType> ticketTypeList = ticketTypeRetriever.findTicketTypeListByRoundIdList(ticketRoundIdList);
            final Map<Long, List<TicketType>> ticketTypesByTicketRoundIdMap = ticketTypeList
                    .stream()
                    .collect(Collectors.groupingBy(TicketType::getTicketRoundId));

            //라운드별로 티켓타입 최소 하나씩은 있는지 검증
            verifyEveryRoundHasTicketType(ticketTypesByTicketRoundIdMap, ticketRoundIdList);


            // Round DTO 리스트 생성
            final List<EventTicketInfoResponse.Round> roundDtoList = ticketRoundList.stream()
                    .map(round -> {
                        boolean isAvailable = !now.isBefore(round.getSalesStartDate()) && !now.isAfter(round.getSalesEndDate());
                        final List<TicketType> ticketTypeListInMap = Objects.requireNonNull(ticketTypesByTicketRoundIdMap.get(round.getTicketRoundId()));
                        final String roundPrice = formatRoundPrice(ticketTypeListInMap);

                        final List<EventTicketInfoResponse.TicketType> ticketTypeDtoList = getTicketTypeIfTicketRoundAvailableOrEmptyList(isAvailable, ticketTypeListInMap);

                        return new EventTicketInfoResponse.Round(
                                round.getTicketRoundId(),
                                isAvailable,
                                roundPrice,
                                round.getTicketRoundTitle(),
                                ticketTypeDtoList
                        );
                    }).toList();

            return new EventTicketInfoResponse(roundDtoList);
        } catch (TicketTypeNotfoundException e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        } catch (PriceFormatException e) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET_TYPE_PRICE);
        }
    }

    private void verifyEveryRoundHasTicketType(final Map<Long, List<TicketType>> ticketTypesByTicketRoundIdMap,
                                               final List<Long> ticketRoundIdList) {
        if (!ticketTypesByTicketRoundIdMap.keySet().containsAll(ticketRoundIdList)) {
            throw new NotFoundTicketException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        }
    }

    private List<Long> extractTicketRoundIdList(final List<TicketRound> ticketRoundList) {
        return ticketRoundList.stream()
                .map(TicketRound::getTicketRoundId)
                .toList();
    }

    private List<EventTicketInfoResponse.TicketType> getTicketTypeIfTicketRoundAvailableOrEmptyList(final boolean isAvailable, final List<TicketType> ticketTypeListInMap) {
        return isAvailable
                ? ticketTypeListInMap.stream()
                .map(this::makeFormattedTicketTypeDto)
                .toList()
                : List.of();
    }

    private String formatRoundPrice(final List<TicketType> ticketTypes) {
        return PriceFormatterUtil.formatRoundPrice(
                ticketTypes.stream()
                        .map(TicketType::getTicketPrice)
                        .toList());
    }

    // 포맷팅된 티켓타입dto로 변환
    private EventTicketInfoResponse.TicketType makeFormattedTicketTypeDto(final TicketType ticketType) {
        final String formattedDate = DateFormatterUtil.formatEventDate(
                ticketType.getTicketStartDate(), ticketType.getTicketEndDate());
        final String formattedTime = ticketType.getTicketStartDate()
                .toLocalTime()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));

        return new EventTicketInfoResponse.TicketType(
                String.valueOf(ticketType.getTicketTypeId()),
                ticketType.getTicketTypeName(),
                formattedDate,
                formattedTime,
                PriceFormatterUtil.formatPrice(ticketType.getTicketPrice())
        );
    }
}
