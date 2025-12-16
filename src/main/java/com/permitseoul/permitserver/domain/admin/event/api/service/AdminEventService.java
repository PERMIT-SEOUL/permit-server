package com.permitseoul.permitserver.domain.admin.event.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.admin.event.api.dto.req.AdminEventImageRequest;
import com.permitseoul.permitserver.domain.admin.event.api.dto.req.AdminEventUpdateRequest;
import com.permitseoul.permitserver.domain.admin.event.api.dto.req.AdminEventWithTicketCreateRequest;
import com.permitseoul.permitserver.domain.admin.event.api.dto.res.AdminEventDetailResponse;
import com.permitseoul.permitserver.domain.admin.event.api.dto.res.AdminEventListResponse;
import com.permitseoul.permitserver.domain.admin.event.core.component.AdminEventRetriever;
import com.permitseoul.permitserver.domain.admin.event.core.component.AdminEventSaver;
import com.permitseoul.permitserver.domain.admin.event.core.component.AdminEventUpdater;
import com.permitseoul.permitserver.domain.admin.event.core.exception.AdminEventNotFoundException;
import com.permitseoul.permitserver.domain.admin.eventimage.core.component.AdminEventImageRemover;
import com.permitseoul.permitserver.domain.admin.eventimage.core.component.AdminEventImageRetriever;
import com.permitseoul.permitserver.domain.admin.eventimage.core.component.AdminEventImageSaver;
import com.permitseoul.permitserver.domain.admin.ticketround.core.component.AdminTicketRoundRetriever;
import com.permitseoul.permitserver.domain.admin.ticketround.core.component.AdminTicketRoundSaver;
import com.permitseoul.permitserver.domain.admin.tickettype.core.component.AdminTicketTypeRetriever;
import com.permitseoul.permitserver.domain.admin.tickettype.core.component.AdminTicketTypeSaver;
import com.permitseoul.permitserver.domain.admin.tickettype.core.component.AdminRedisTicketTypeSaver;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.entity.EventEntity;
import com.permitseoul.permitserver.domain.event.core.exception.EventIllegalArgumentException;
import com.permitseoul.permitserver.domain.eventimage.core.domain.EventImage;
import com.permitseoul.permitserver.domain.eventimage.core.domain.entity.EventImageEntity;
import com.permitseoul.permitserver.domain.sitemapimage.core.component.EventSiteMapImageSaver;
import com.permitseoul.permitserver.domain.sitemapimage.core.domain.entity.EventSiteMapImageEntity;
import com.permitseoul.permitserver.domain.ticketround.core.domain.TicketRound;
import com.permitseoul.permitserver.domain.ticketround.core.exception.TicketRoundIllegalArgumentException;
import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeIllegalException;
import com.permitseoul.permitserver.global.exception.DateFormatException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.util.LocalDateTimeFormatterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.IntStream;


@Service
@RequiredArgsConstructor
public class AdminEventService {

    private final AdminEventRetriever adminEventRetriever;
    private final AdminTicketRoundRetriever adminTicketRoundRetriever;
    private final AdminTicketTypeRetriever adminTicketTypeRetriever;
    private final AdminEventImageRetriever adminEventImageRetriever;
    private final AdminEventSaver adminEventSaver;
    private final AdminEventImageSaver adminEventImageSaver;
    private final AdminTicketRoundSaver adminTicketRoundSaver;
    private final AdminTicketTypeSaver adminTicketTypeSaver;
    private final AdminEventUpdater adminEventUpdater;
    private final AdminEventImageRemover adminEventImageRemover;
    private final AdminRedisTicketTypeSaver adminRedisTicketTypeSaver;
    private final EventSiteMapImageSaver eventSiteMapImageSaver;

    @Transactional(readOnly = true)
    public List<AdminEventListResponse> getEvents() {
        final List<Event> events = adminEventRetriever.getAllEvents();
        if (isEmpty(events)) return List.of();

        final Map<Long, Integer> eventIdToSoldTicketCount = initSoldTicketCountZero(events);

        // 판매 티켓 수 계산
        aggregateSoldCounts(events, eventIdToSoldTicketCount);

        final List<Event> sorted = sortEventsByStartDateDesc(events);
        final Map<String, List<AdminEventListResponse.AdminEventInfo>> groupedByYearMonth = groupEventsByYearMonth(sorted, eventIdToSoldTicketCount);

        final List<AdminEventListResponse> result = new ArrayList<>();
        for (Map.Entry<String, List<AdminEventListResponse.AdminEventInfo>> entry : groupedByYearMonth.entrySet()) {
            result.add(AdminEventListResponse.of(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public AdminEventDetailResponse getEventDetail(final long eventId) {
        try {
            final Event event = adminEventRetriever.findEventById(eventId);
            final List<EventImage> eventImages = adminEventImageRetriever.findAllEventImagesByEventId(event.getEventId());

            final List<AdminEventDetailResponse.AdminEventImageInfo> adminEventImageInfos = eventImages.stream()
                    .sorted(Comparator.comparingInt(EventImage::getSequence))
                    .map(eventImage -> AdminEventDetailResponse.AdminEventImageInfo.of(eventImage.getImageUrl()))
                    .toList();

            return AdminEventDetailResponse.of(
                    event.getEventId(),
                    LocalDateTimeFormatterUtil.formatyyyyMMdd(event.getVisibleStartAt()),
                    LocalDateTimeFormatterUtil.formatHHmm(event.getVisibleStartAt()),
                    LocalDateTimeFormatterUtil.formatyyyyMMdd(event.getVisibleEndAt()),
                    LocalDateTimeFormatterUtil.formatHHmm(event.getVisibleEndAt()),
                    event.getTicketCheckCode(),
                    event.getName(),
                    event.getEventType(),
                    LocalDateTimeFormatterUtil.formatyyyyMMdd(event.getStartAt()),
                    LocalDateTimeFormatterUtil.formatHHmm(event.getStartAt()),
                    LocalDateTimeFormatterUtil.formatyyyyMMdd(event.getEndAt()),
                    LocalDateTimeFormatterUtil.formatHHmm(event.getEndAt()),
                    event.getVenue(),
                    event.getLineUp(),
                    event.getDetails(),
                    adminEventImageInfos,
                    event.getMinAge()
            );

        } catch(AdminEventNotFoundException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_EVENT);
        }
    }

    @Transactional
    public void createEventWithTickets(final AdminEventWithTicketCreateRequest createEventWithTicketsReq) {
        final LocalDateTime eventStartDateTime = combineDateTime(createEventWithTicketsReq.startDate(), createEventWithTicketsReq.startTime());
        final LocalDateTime eventEndDateTime = combineDateTime(createEventWithTicketsReq.endDate(), createEventWithTicketsReq.endTime());

        final LocalDateTime eventExposureStartDateTime = combineDateTime(createEventWithTicketsReq.eventExposureStartDate(), createEventWithTicketsReq.eventExposureStartTime());
        final LocalDateTime eventExposureEndDateTime = combineDateTime(createEventWithTicketsReq.eventExposureEndDate(), createEventWithTicketsReq.eventExposureEndTime());

        final LocalDateTime ticketRoundSalesStartDateTime = combineDateTime(createEventWithTicketsReq.roundSalesStartDate(), createEventWithTicketsReq.roundSalesStartTime());
        final LocalDateTime ticketRoundSalesEndDateTime = combineDateTime(createEventWithTicketsReq.roundSalesEndDate(), createEventWithTicketsReq.roundSalesEndTime());

        final Event savedEvent = saveEvent(createEventWithTicketsReq, eventStartDateTime, eventEndDateTime, eventExposureStartDateTime, eventExposureEndDateTime);
        saveEventImages(savedEvent.getEventId(), createEventWithTicketsReq.images());

        saveEventSiteMapImages(savedEvent.getEventId(), createEventWithTicketsReq.siteMapImages());

        final TicketRound savedTicketRound;
        final List<TicketType> savedTicketTypes;
        try {
            savedTicketRound = saveTicketRound(
                    savedEvent.getEventId(),
                    createEventWithTicketsReq.ticketRoundName(),
                    ticketRoundSalesStartDateTime,
                    ticketRoundSalesEndDateTime
            );
            savedTicketTypes = saveTicketTypes(createEventWithTicketsReq.ticketTypes(), savedTicketRound.getTicketRoundId());
        } catch (TicketRoundIllegalArgumentException | TicketTypeIllegalException e) {
            throw new AdminApiException(ErrorCode.BAD_REQUEST_DATE_TIME_ERROR);
        }

        //redis 등록
        try {
            adminRedisTicketTypeSaver.saveTicketTypesInRedis(savedTicketTypes);
        } catch (Exception e) {
            throw new AdminApiException(ErrorCode.INTERNAL_TICKET_TYPE_REDIS_ERROR);
        }
    }

    @Transactional
    public void updateEvent(final AdminEventUpdateRequest updateRequest) {
        try {
            final EventEntity eventEntity = adminEventRetriever.findEventEntityById(updateRequest.eventId());
            final LocalDateTime visibleStartAt = combineDateAndTimeForUpdate(
                    updateRequest.eventExposureStartDate(),
                    updateRequest.eventExposureStartTime(),
                    eventEntity.getVisibleStartAt()
            );
            final LocalDateTime visibleEndAt = combineDateAndTimeForUpdate(
                    updateRequest.eventExposureEndDate(),
                    updateRequest.eventExposureEndTime(),
                    eventEntity.getVisibleEndAt()
            );

            final LocalDateTime startAt = combineDateAndTimeForUpdate(updateRequest.startDate(), updateRequest.startTime(), eventEntity.getStartAt());
            final LocalDateTime endAt = combineDateAndTimeForUpdate(updateRequest.endDate(), updateRequest.endTime(), eventEntity.getEndAt());
            adminEventUpdater.updateEvent(eventEntity, updateRequest, visibleStartAt, visibleEndAt, startAt, endAt);
            updateEventImages(eventEntity.getEventId(), updateRequest.images());

        } catch(AdminEventNotFoundException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_EVENT);
        } catch(EventIllegalArgumentException | DateFormatException e) {
            throw new AdminApiException(ErrorCode.BAD_REQUEST_DATE_TIME_ERROR);
        }
    }

    private void updateEventImages(final long eventId, final List<AdminEventImageRequest> eventImages) {
        if (eventImages != null && !eventImages.isEmpty()) {
            adminEventImageRemover.deleteAllByEventId(eventId);
            saveEventImages(eventId, eventImages);
        }
    }

    private void saveEventSiteMapImages(final long eventId, final List<AdminEventImageRequest> eventSiteMapImages) {
        if(eventSiteMapImages == null || eventSiteMapImages.isEmpty()) {
            return;
        }
        final List<EventSiteMapImageEntity> eventSiteMapImageEntities = IntStream.range(0, eventSiteMapImages.size())
                .mapToObj(i -> EventSiteMapImageEntity.create(
                        eventSiteMapImages.get(i).imageUrl().trim(),
                        i,
                        eventId
                        ))
                .toList();
        eventSiteMapImageSaver.saveSiteMapImages(eventSiteMapImageEntities);
    }

    private void saveEventImages(final long eventId, final List<AdminEventImageRequest> eventImages) {
        final List<EventImageEntity> eventImageEntityList = IntStream.range(0, eventImages.size())
                .mapToObj(i -> EventImageEntity.create(
                        eventId,
                        eventImages.get(i).imageUrl().trim(),
                        i
                ))
                .toList();
        adminEventImageSaver.saveEventImages(eventImageEntityList);
    }

    private LocalDateTime combineDateAndTimeForUpdate(final LocalDate date,
                                                      final LocalTime time,
                                                      final LocalDateTime originalDateTime
    ) {
        return LocalDateTimeFormatterUtil.combineDateAndTimeForUpdate(date, time, originalDateTime);
    }

    private List<TicketType> saveTicketTypes(final List<AdminEventWithTicketCreateRequest.TicketTypeRequest> ticketTypes,
                                             final long ticketRoundId) {
        final List<TicketTypeEntity> ticketTypeEntityList = ticketTypes.stream()
                .map(ticketType -> TicketTypeEntity.create(
                        ticketRoundId,
                        ticketType.ticketName(),
                        ticketType.price(),
                        ticketType.ticketCount(),
                        LocalDateTime.of(ticketType.ticketStartDate(), ticketType.ticketStartTime()),
                        LocalDateTime.of(ticketType.ticketEndDate(), ticketType.ticketEndTime())
                )).toList();
        return adminTicketTypeSaver.saveAllTicketTypes(ticketTypeEntityList);
    }

    private TicketRound saveTicketRound(final long eventId,
                                        final String ticketRoundName,
                                        final LocalDateTime salesStartDateTime,
                                        final LocalDateTime salesEndDateTime
    ) {
        return adminTicketRoundSaver.saveTicketRound(
                eventId,
                ticketRoundName,
                salesStartDateTime,
                salesEndDateTime
        );
    }

    private Event saveEvent(final AdminEventWithTicketCreateRequest req,
                            final LocalDateTime eventStartDateTime,
                            final LocalDateTime eventEndDateTime,
                            final LocalDateTime eventExposureStartDateTime,
                            final LocalDateTime eventExposureEndDateTime
    ) {
        return adminEventSaver.saveEvent(
                req.name(),
                req.eventType(),
                eventStartDateTime,
                eventEndDateTime,
                req.venue(),
                req.lineup(),
                req.details(),
                req.minAge(),
                eventExposureStartDateTime,
                eventExposureEndDateTime,
                req.verificationCode()
        );
    }

    private LocalDateTime combineDateTime(final LocalDate date, final LocalTime time) {
        return LocalDateTimeFormatterUtil.combineDateAndTime(date, time);
    }

    private Map<Long, Integer> initSoldTicketCountZero(final List<Event> events) {
        final Map<Long, Integer> map = new HashMap<>();
        for (Event e : events) {
            if (e != null) map.put(e.getEventId(), 0);
        }
        return map;
    }

    private void aggregateSoldCounts(final List<Event> events, final Map<Long, Integer> soldByEventId) {
        final List<Long> eventIds = extractEventIds(events);

        final List<TicketRound> rounds = adminTicketRoundRetriever.getTicketRoundsByEventIds(eventIds);
        if (isEmpty(rounds)) {
            return;
        }

        // roundId -> eventId 매핑
        final Map<Long, Long> roundToEvent = buildRoundToEventMap(rounds);

        final List<Long> ticketRoundIds = extractTicketRoundIds(rounds);
        if (ticketRoundIds.isEmpty()) {
            return;
        }

        final List<TicketType> ticketTypes = adminTicketTypeRetriever.getTicketTypesByTicketRounds(ticketRoundIds);
        if (isEmpty(ticketTypes)) {
            return;
        }
        computeSoldByEvent(ticketTypes, roundToEvent, soldByEventId);
    }

    private List<Long> extractEventIds(final List<Event> events) {
        final List<Long> eventIds = new ArrayList<>();
        for (Event e : events) {
            eventIds.add(e.getEventId());
        }
        return eventIds;
    }

    // roundId -> eventId 매핑
    private Map<Long, Long> buildRoundToEventMap(final List<TicketRound> rounds) {
        final Map<Long, Long> map = new HashMap<>();
        for (TicketRound r : rounds) {
            map.put(r.getTicketRoundId(), r.getEventId());
        }
        return map;
    }

    private List<Long> extractTicketRoundIds(final List<TicketRound> rounds) {
        final List<Long> roundIds = new ArrayList<>();
        for (TicketRound r : rounds) {
            roundIds.add(r.getTicketRoundId());
        }
        return roundIds;
    }

    //타입단위로 팔린 티켓 개수 구함
    private void computeSoldByEvent(final List<TicketType> ticketTypes,
                                    final Map<Long, Long> roundToEvent,
                                    final Map<Long, Integer> soldByEventId
    ) {
        for (TicketType ticketType : ticketTypes) {
            final long eventId = roundToEvent.get(ticketType.getTicketRoundId());
            final int total = Math.max(0, ticketType.getTotalTicketCount());
            final int remain = Math.max(0, ticketType.getRemainTicketCount());
            final int sold = Math.max(0, total - remain);
            soldByEventId.merge(eventId, sold, Integer::sum);
        }
    }

    //최신순 정렬
    private List<Event> sortEventsByStartDateDesc(final List<Event> events) {
        final List<Event> sorted = new ArrayList<>(events);
        sorted.sort(Comparator.comparing(Event::getStartAt).reversed());
        return sorted;
    }

    private Map<String, List<AdminEventListResponse.AdminEventInfo>> groupEventsByYearMonth(
            final List<Event> sortedEvents,
            final Map<Long, Integer> soldByEventId
    ) {
        final Map<String, List<AdminEventListResponse.AdminEventInfo>> grouped = new LinkedHashMap<>();
        for (Event e : sortedEvents) {
            final LocalDateTime start = e.getStartAt();
            final String yearAndMonth = LocalDateTimeFormatterUtil.formatYearMonth(start);   // "yyyy.MM"
            final String day = LocalDateTimeFormatterUtil.formatDayWithDate(start); // "E, dd"

            grouped.computeIfAbsent(yearAndMonth, k -> new ArrayList<>())
                    .add(AdminEventListResponse.AdminEventInfo.of(
                            e.getEventId(),
                            e.getName(),
                            e.getVenue(),
                            day,
                            soldByEventId.getOrDefault(e.getEventId(), 0)
                    ));
        }
        return grouped;
    }

    private boolean isEmpty(final List<?> list) {
        return list == null || list.isEmpty();
    }
}
