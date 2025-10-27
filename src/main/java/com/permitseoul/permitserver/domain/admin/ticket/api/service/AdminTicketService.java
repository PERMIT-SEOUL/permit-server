package com.permitseoul.permitserver.domain.admin.ticket.api.service;

import com.permitseoul.permitserver.domain.admin.base.api.exception.AdminApiException;
import com.permitseoul.permitserver.domain.admin.ticket.api.dto.TicketTypeSplitResult;
import com.permitseoul.permitserver.domain.admin.ticket.api.dto.req.TicketRoundWithTypeCreateRequest;
import com.permitseoul.permitserver.domain.admin.ticket.api.dto.req.TicketRoundWithTypeUpdateRequest;
import com.permitseoul.permitserver.domain.admin.ticket.api.dto.res.TicketRoundAndTicketTypeRes;
import com.permitseoul.permitserver.domain.admin.ticket.api.dto.res.TicketRoundAndTypeDetailRes;
import com.permitseoul.permitserver.domain.admin.ticket.core.component.AdminTicketRetriever;
import com.permitseoul.permitserver.domain.admin.ticketround.core.component.AdminTicketRoundRetriever;
import com.permitseoul.permitserver.domain.admin.ticketround.core.component.AdminTicketRoundSaver;
import com.permitseoul.permitserver.domain.admin.ticketround.core.component.AdminTicketRoundUpdater;
import com.permitseoul.permitserver.domain.admin.ticketround.exception.AdminTicketRoundNotFoundException;
import com.permitseoul.permitserver.domain.admin.tickettype.core.component.AdminTicketTypeRetriever;
import com.permitseoul.permitserver.domain.admin.tickettype.core.component.AdminTicketTypeSaver;
import com.permitseoul.permitserver.domain.admin.tickettype.core.component.AdminTicketTypeUpdater;
import com.permitseoul.permitserver.domain.admin.tickettype.core.component.AdminRedisTicketTypeSaver;
import com.permitseoul.permitserver.domain.admin.tickettype.core.exception.AdminTicketTypeNotFoundException;
import com.permitseoul.permitserver.domain.ticket.core.domain.TicketStatus;
import com.permitseoul.permitserver.domain.ticketround.core.component.TicketRoundRetriever;
import com.permitseoul.permitserver.domain.ticketround.core.domain.TicketRound;
import com.permitseoul.permitserver.domain.ticketround.core.domain.entity.TicketRoundEntity;
import com.permitseoul.permitserver.domain.ticketround.core.exception.TicketRoundIllegalArgumentException;
import com.permitseoul.permitserver.domain.ticketround.core.exception.TicketRoundNotFoundException;
import com.permitseoul.permitserver.domain.tickettype.core.component.TicketTypeUpdater;
import com.permitseoul.permitserver.domain.tickettype.core.domain.TicketType;
import com.permitseoul.permitserver.domain.tickettype.core.domain.entity.TicketTypeEntity;
import com.permitseoul.permitserver.domain.tickettype.core.exception.TicketTypeIllegalException;
import com.permitseoul.permitserver.global.Constants;
import com.permitseoul.permitserver.global.redis.RedisManager;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.util.DateFormatterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminTicketService {
    private final AdminTicketTypeRetriever adminTicketTypeRetriever;
    private final AdminTicketRoundRetriever adminTicketRoundRetriever;
    private final AdminTicketRetriever adminTicketRetriever;
    private final AdminTicketRoundSaver adminTicketRoundSaver;
    private final AdminTicketTypeSaver adminTicketTypeSaver;
    private final AdminTicketRoundUpdater adminTicketRoundUpdater;
    private final AdminRedisTicketTypeSaver adminRedisTicketTypeSaver;
    private final RedisManager redisManager;

    private static final int EMPTY_TICKET_COUNT_ZERO = 0;
    private static final List<TicketStatus> SOLD_STATUSES = List.of(TicketStatus.RESERVED, TicketStatus.USED);
    private final TicketRoundRetriever ticketRoundRetriever;
    private final AdminTicketTypeUpdater adminTicketTypeUpdater;
    private final TicketTypeUpdater ticketTypeUpdater;

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

    @Transactional
    public void createTicketRoundWithType(final long eventId,
                                          final String ticketRoundName,
                                          final LocalDateTime roundSalesStartDate,
                                          final LocalDateTime roundSalesEndDate,
                                          final List<TicketRoundWithTypeCreateRequest.TicketTypeRequest> ticketTypeRequests) {
        final List<TicketType> savedTicketTypes;
        try {
            final TicketRound savedTicketRound = adminTicketRoundSaver.saveTicketRound(eventId, ticketRoundName, roundSalesStartDate, roundSalesEndDate);
            savedTicketTypes = saveTicketTypes(ticketTypeRequests, savedTicketRound.getTicketRoundId());
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
    public void updateTicketRoundWithType(final TicketRoundWithTypeUpdateRequest updateRequest) {
        final TicketTypeSplitResult ticketTypeSplitResult;
        try {
            final TicketRoundEntity ticketRoundEntity =
                    ticketRoundRetriever.findTicketRoundEntityById(updateRequest.ticketRoundId());
            adminTicketRoundUpdater.updateTicketRound(
                    ticketRoundEntity,
                    updateRequest.ticketRoundName(),
                    updateRequest.ticketRoundSalesStartDate(),
                    updateRequest.ticketRoundSalesEndDate()
            );

            ticketTypeSplitResult = splitTicketTypeNewAndUpdate(ticketRoundEntity, updateRequest.ticketTypes());

            if (!ticketTypeSplitResult.newTicketTypes().isEmpty()) {
                adminTicketTypeSaver.saveAllTicketTypes(ticketTypeSplitResult.newTicketTypes());
            }
        } catch (TicketRoundNotFoundException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_TICKET_ROUND);
        } catch (TicketRoundIllegalArgumentException | TicketTypeIllegalException e) {
            throw new AdminApiException(ErrorCode.BAD_REQUEST_DATE_TIME_ERROR);
        } catch (AdminTicketTypeNotFoundException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        }

        syncRedisTicketCounts(ticketTypeSplitResult.newTicketTypes(), ticketTypeSplitResult.updatedTicketTypes());
    }

    private void syncRedisTicketCounts(
            final List<TicketTypeEntity> newTicketTypes,
            final List<TicketTypeEntity> updatedTicketTypes
    ) {
        // 신규 ticketTYpe Redis 등록
        for (TicketTypeEntity entity : newTicketTypes) {
            final String key = Constants.REDIS_TICKET_TYPE_KEY_NAME + entity.getTicketTypeId() + Constants.REDIS_TICKET_TYPE_REMAIN;
            try {
                redisManager.set(key, String.valueOf(entity.getRemainTicketCount()), null);
            } catch (Exception e) {
                //redis 롤백해야됨 이전것들
                log.error("[RedisManager] 신규 티켓 등록 실패 key={}", key, e);
                throw new AdminApiException(ErrorCode.INTERNAL_TICKET_TYPE_REDIS_ERROR);
            }
        }

        // 기존 ticketType Redis 잔여 개수 업데이트
        for (TicketTypeEntity entity : updatedTicketTypes) {
            final String key = Constants.REDIS_TICKET_TYPE_KEY_NAME + entity.getTicketTypeId() + Constants.REDIS_TICKET_TYPE_REMAIN;
            try {
                final String existRedisTicketTypeRemainCount = redisManager.get(key);
                if (existRedisTicketTypeRemainCount == null) {
                    //redis 롤백해야됨 이전것들
                    log.error("[RedisManager] 존재하지 않는 key 감지 key={}", key);
                    throw new AdminApiException(ErrorCode.INTERNAL_TICKET_TYPE_NOT_FOUND_REDIS_ERROR); // 커스텀 에러코드
                }

                final int dbRemainTicketTypeCount = entity.getRemainTicketCount();
                final int redisTicketTypeRemainCount = Integer.parseInt(existRedisTicketTypeRemainCount);

                int diff = dbRemainTicketTypeCount - redisTicketTypeRemainCount;

                int newRedisValue = redisTicketTypeRemainCount + diff;
                if (newRedisValue < 0) {
                    newRedisValue = 0;
                }

                redisManager.set(key, String.valueOf(newRedisValue), null);
                log.info("[Redis] 기존 티켓 갱신 key={}, old={}, new={}", key, redisTicketTypeRemainCount, newRedisValue);
            } catch (Exception e) {
                //redis 롤백해야됨 이전것들
                log.error("[Redis] 기존 티켓 갱신 실패 key={}", key, e);
                throw e; // rollback 유도
            }
        }
    }

    private TicketTypeSplitResult splitTicketTypeNewAndUpdate(
            final TicketRoundEntity ticketRoundEntity,
            final List<TicketRoundWithTypeUpdateRequest.TicketTypeUpdateRequest> ticketTypeRequests
    ) {
        final List<TicketTypeEntity> newTicketTypeEntities = new ArrayList<>();
        final List<TicketTypeEntity> updatedTicketTypeEntities = new ArrayList<>();

        for (TicketRoundWithTypeUpdateRequest.TicketTypeUpdateRequest ticketTypeReq : ticketTypeRequests) {
            if (ticketTypeReq.id() == null) { // 신규 ticketType
                final TicketTypeEntity newEntity = TicketTypeEntity.create(
                        ticketRoundEntity.getTicketRoundId(),
                        ticketTypeReq.name(),
                        ticketTypeReq.price(),
                        ticketTypeReq.totalCount(),
                        ticketTypeReq.startDate(),
                        ticketTypeReq.endDate()
                );
                newTicketTypeEntities.add(newEntity);
            } else { // 기존 ticketType
                final TicketTypeEntity existTicketTypeEntity = adminTicketTypeRetriever.getTicketTypeEntityById(ticketTypeReq.id());
                if (!Objects.equals(existTicketTypeEntity.getTicketRoundId(), ticketRoundEntity.getTicketRoundId())) {
                    throw new AdminApiException(ErrorCode.BAD_REQUEST_MISMATCH_TICKET_TYPE_ROUND);
                }
                adminTicketTypeUpdater.updateTicketType(
                        existTicketTypeEntity,
                        ticketTypeReq.name(),
                        ticketTypeReq.price(),
                        ticketTypeReq.totalCount(),
                        ticketTypeReq.startDate(),
                        ticketTypeReq.endDate()
                );
                updatedTicketTypeEntities.add(existTicketTypeEntity);
            }
        }

        return new TicketTypeSplitResult(newTicketTypeEntities, updatedTicketTypeEntities);
    }

    private List<TicketType> saveTicketTypes(final List<TicketRoundWithTypeCreateRequest.TicketTypeRequest> ticketTypes,
                                             final long ticketRoundId) {
        final List<TicketTypeEntity> ticketTypeEntityList = ticketTypes.stream()
                .map(ticketType -> TicketTypeEntity.create(
                        ticketRoundId,
                        ticketType.name(),
                        BigDecimal.valueOf(ticketType.price()),
                        ticketType.totalCount(),
                        ticketType.startDate(),
                        ticketType.endDate()
                )).toList();
        return adminTicketTypeSaver.saveAllTicketTypes(ticketTypeEntityList);
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
        if (ticketTypes.isEmpty()) {
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