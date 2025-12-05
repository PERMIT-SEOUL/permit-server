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
import com.permitseoul.permitserver.global.util.LocalDateTimeFormatterUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
                LocalDateTimeFormatterUtil.formatyyyyMMdd(ticketRound.getSalesStartAt()),
                LocalDateTimeFormatterUtil.formatHHmm(ticketRound.getSalesStartAt()),
                LocalDateTimeFormatterUtil.formatyyyyMMdd(ticketRound.getSalesEndAt()),
                LocalDateTimeFormatterUtil.formatHHmm(ticketRound.getSalesEndAt()),
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
        final TicketTypeSplitResult ticketTypeSplitByOldAndNewDto;
        try {
            final TicketRoundEntity ticketRoundEntity =
                    ticketRoundRetriever.findTicketRoundEntityById(updateRequest.ticketRoundId());
            adminTicketRoundUpdater.updateTicketRound(
                    ticketRoundEntity,
                    updateRequest.ticketRoundName(),
                    updateRequest.ticketRoundSalesStartDate(),
                    updateRequest.ticketRoundSalesEndDate()
            );

            ticketTypeSplitByOldAndNewDto = splitTicketTypeNewAndUpdate(ticketRoundEntity, updateRequest.ticketTypes());

            if (!ticketTypeSplitByOldAndNewDto.newTicketTypes().isEmpty()) {
                adminTicketTypeSaver.saveAllTicketTypes(ticketTypeSplitByOldAndNewDto.newTicketTypes());
            }
        } catch (TicketRoundNotFoundException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_TICKET_ROUND);
        } catch (TicketRoundIllegalArgumentException | TicketTypeIllegalException e) {
            throw new AdminApiException(ErrorCode.BAD_REQUEST_DATE_TIME_ERROR);
        } catch (AdminTicketTypeNotFoundException e) {
            throw new AdminApiException(ErrorCode.NOT_FOUND_TICKET_TYPE);
        }

        syncRedisTicketCounts(ticketTypeSplitByOldAndNewDto);
    }

    private void syncRedisTicketCounts(final TicketTypeSplitResult ticketTypeSplitResult) {
        final List<String> newRedisTicketTypeKeys = new ArrayList<>();
        final Map<String, String> existRedisTicketType = new HashMap<>();

        try {
            // 새로운 ticketType 등록
            for (TicketTypeEntity newTicketTypeEntity : ticketTypeSplitResult.newTicketTypes()) {
                final String key = Constants.REDIS_TICKET_TYPE_KEY_NAME + newTicketTypeEntity.getTicketTypeId() + Constants.REDIS_TICKET_TYPE_REMAIN;
                redisManager.set(key, String.valueOf(newTicketTypeEntity.getTotalTicketCount()), null);
                newRedisTicketTypeKeys.add(key);
                log.info("[Redis] 신규 ticketType 등록 key={}, ticketTypeCount={}", key, newTicketTypeEntity.getTotalTicketCount());
            }

            // 기존에 있던 ticketType 업데이트
            for (TicketTypeEntity updatedTicketTypeEntity : ticketTypeSplitResult.updatedTicketTypes()) {
                final String key = Constants.REDIS_TICKET_TYPE_KEY_NAME + updatedTicketTypeEntity.getTicketTypeId() + Constants.REDIS_TICKET_TYPE_REMAIN;
                final String existTicketTypeCount = redisManager.get(key);
                existRedisTicketType.put(key, existTicketTypeCount);
                if (existTicketTypeCount == null) {
                    throw new AdminApiException(ErrorCode.INTERNAL_TICKET_TYPE_NOT_FOUND_REDIS_ERROR);
                }

                final int diff = ticketTypeSplitResult.updateDiffMap().get(updatedTicketTypeEntity.getTicketTypeId());
                if (diff == 0) continue;

                final Long newRemain = redisManager.increment(key, diff);
                if (newRemain == null) {
                    throw new AdminApiException(ErrorCode.INTERNAL_TICKET_TYPE_REDIS_ERROR);
                }

                if (newRemain <= 0) {
                    redisManager.set(key, "0", null);
                    log.warn("[Redis] ticketType 품절 key={}, ",key);
                } else {
                    log.info("[Redis] 기존 ticketType update complete, key={}, diff={}, remain={}", key, diff, newRemain);
                }
            }
        } catch (Exception e) {
            log.error("[Redis] 오류 발생 → Redis 롤백 수행 시작", e);
            rollbackRedisChanges(ticketTypeSplitResult, existRedisTicketType, newRedisTicketTypeKeys);
            throw new AdminApiException(ErrorCode.INTERNAL_TICKET_TYPE_REDIS_ERROR);
        }
    }

    private void rollbackRedisChanges(final TicketTypeSplitResult splitResult,
                                      final Map<String, String> successExistRedisTicketType,
                                      final List<String> successCreatedRedisTicketTypeKeys) {
        // 기존에 있던 ticketTypes
        for (TicketTypeEntity updatedTicketEntity : splitResult.updatedTicketTypes()) {
            final String key = Constants.REDIS_TICKET_TYPE_KEY_NAME + updatedTicketEntity.getTicketTypeId() + Constants.REDIS_TICKET_TYPE_REMAIN;
            if (!successExistRedisTicketType.containsKey(key)) continue;

            final int diff = splitResult.updateDiffMap().get(updatedTicketEntity.getTicketTypeId());
            if (diff == 0) continue;

            try {
                final String previousValue = successExistRedisTicketType.get(key);
                redisManager.set(key, previousValue, null);
                log.info("[RedisRollback] 기존 key ticketType 복구 key={}, remainCount={}", key, previousValue);
            } catch (Exception e) {
                log.error("[RedisRollback] 기존 key 롤백 실패 key={}, diff={}", key, diff, e);
            }
        }

        // 새로 만든 ticketTypes
        for (String key : successCreatedRedisTicketTypeKeys) {
            try {
                redisManager.delete(key);
                log.info("[RedisRollback] 신규 key 삭제 key={}", key);
            } catch (Exception e) {
                log.error("[RedisRollback] 신규 key 삭제 실패 key={}", key, e);
            }
        }
    }

    private TicketTypeSplitResult splitTicketTypeNewAndUpdate(
            final TicketRoundEntity ticketRoundEntity,
            final List<TicketRoundWithTypeUpdateRequest.TicketTypeUpdateRequest> ticketTypeRequests
    ) {
        final List<TicketTypeEntity> newTicketTypeEntities = new ArrayList<>();
        final List<TicketTypeEntity> updatedTicketTypeEntities = new ArrayList<>();
        final Map<Long, Integer> updateDiffMap = new HashMap<>();

        // 기존에 있던 ticketType id들만 추출
        final List<Long> alreadyExistTicketTypeIds = ticketTypeRequests.stream()
                .map(TicketRoundWithTypeUpdateRequest.TicketTypeUpdateRequest::id)
                .filter(Objects::nonNull)
                .toList();

        final Map<Long, TicketTypeEntity> alreadyExistTicketTypeEntityMap = alreadyExistTicketTypeIds.isEmpty()
                ? Map.of()
                : adminTicketTypeRetriever.getTicketTypeEntitiesByIds(alreadyExistTicketTypeIds).stream()
                .collect(Collectors.toMap(TicketTypeEntity::getTicketTypeId, e -> e));

        for (TicketRoundWithTypeUpdateRequest.TicketTypeUpdateRequest ticketTypeReq : ticketTypeRequests) {
            if (ticketTypeReq.id() == null) { // 새로운 ticketType인 경우
                final TicketTypeEntity newEntity = TicketTypeEntity.create(
                        ticketRoundEntity.getTicketRoundId(),
                        ticketTypeReq.name(),
                        ticketTypeReq.price(),
                        ticketTypeReq.totalCount(),
                        ticketTypeReq.startDate(),
                        ticketTypeReq.endDate()
                );
                newTicketTypeEntities.add(newEntity);
            } else { // 원래 있던 ticketType인 경우
                final TicketTypeEntity existTicketTypeEntity = alreadyExistTicketTypeEntityMap.get(ticketTypeReq.id());
                if (existTicketTypeEntity == null) {
                    throw new AdminApiException(ErrorCode.NOT_FOUND_TICKET_TYPE);
                }
                if (!Objects.equals(existTicketTypeEntity.getTicketRoundId(), ticketRoundEntity.getTicketRoundId())) {
                    throw new AdminApiException(ErrorCode.BAD_REQUEST_MISMATCH_TICKET_TYPE_ROUND);
                }

                final int oldTicketTypeTotalCount = existTicketTypeEntity.getTotalTicketCount();
                final int reqTicketTypeTotalCount = ticketTypeReq.totalCount();
                final int ticketTypeTotalDiffCount = reqTicketTypeTotalCount - oldTicketTypeTotalCount;

                adminTicketTypeUpdater.updateTicketType(
                        existTicketTypeEntity,
                        ticketTypeReq.name(),
                        ticketTypeReq.price(),
                        ticketTypeReq.totalCount(),
                        ticketTypeReq.startDate(),
                        ticketTypeReq.endDate()
                );
                updateDiffMap.put(existTicketTypeEntity.getTicketTypeId(), ticketTypeTotalDiffCount);
                updatedTicketTypeEntities.add(existTicketTypeEntity);
            }
        }

        return TicketTypeSplitResult.of(newTicketTypeEntities, updatedTicketTypeEntities, updateDiffMap);
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
                LocalDateTimeFormatterUtil.formatyyyyMMdd(round.getSalesStartAt()),
                LocalDateTimeFormatterUtil.formatHHmm(round.getSalesStartAt()),
                LocalDateTimeFormatterUtil.formatyyyyMMdd(round.getSalesEndAt()),
                LocalDateTimeFormatterUtil.formatHHmm(round.getSalesEndAt()),
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
                                LocalDateTimeFormatterUtil.formatyyyyMMdd(ticketType.getTicketStartAt()),
                                LocalDateTimeFormatterUtil.formatHHmm(ticketType.getTicketStartAt()),
                                LocalDateTimeFormatterUtil.formatyyyyMMdd(ticketType.getTicketEndAt()),
                                LocalDateTimeFormatterUtil.formatHHmm(ticketType.getTicketEndAt())
                        )
                )
                .toList();
    }
}