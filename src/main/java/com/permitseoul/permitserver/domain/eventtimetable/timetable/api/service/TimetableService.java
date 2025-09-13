package com.permitseoul.permitserver.domain.eventtimetable.timetable.api.service;

import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.area.core.component.TimetableAreaRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.area.core.domain.TimetableArea;
import com.permitseoul.permitserver.domain.eventtimetable.area.core.exception.TimetableAreaNotFoundException;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.TimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.TimetableBlock;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.exception.TimetableBlockNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.component.TimetableCategoryRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.TimetableCategory;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.exception.TimetableCategoryNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.dto.TimetableDetailResponse;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.dto.TimetableResponse;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.exception.NotfoundTimetableException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.component.TimetableRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.exception.TimetableNotFoundException;
import com.permitseoul.permitserver.domain.eventtimetable.userlike.core.component.TimetableUserLikeRetriever;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import com.permitseoul.permitserver.global.util.SecureUrlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class TimetableService {
    private final TimetableRetriever timetableRetriever;
    private final TimetableAreaRetriever timetableAreaRetriever;
    private final TimetableCategoryRetriever timetableCategoryRetriever;
    private final TimetableBlockRetriever timetableBlockRetriever;
    private final TimetableUserLikeRetriever timetableUserLikeRetriever;
    private final EventRetriever eventRetriever;
    private final SecureUrlUtil secureUrlUtil;

    @Transactional(readOnly = true)
    public TimetableResponse getEventTimetable(final long eventId, final Long userId) {
        final Event event;
        final Timetable timetable;
        final List<TimetableArea> areaList;
        final List<TimetableCategory> categoryList;

        final List<TimetableBlock> blockList;
        try {
            event = eventRetriever.findEventById(eventId);
            timetable = timetableRetriever.getTimetableByEventId(eventId);
            final long timetableId = timetable.getTimetableId();
            areaList = timetableAreaRetriever.findTimetableAreaListByTimetableId(timetableId);
            categoryList = timetableCategoryRetriever.findAllTimetableCategory(timetableId);
            blockList = timetableBlockRetriever.findAllTimetableBlockByTimetableId(timetableId);

        } catch (EventNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_EVENT);
        } catch (TimetableNotFoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE);
        } catch (TimetableAreaNotFoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_AREA);
        } catch (TimetableCategoryNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_CATEGORY);
        } catch (TimetableBlockNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_BLOCK);
        }

        final List<Long> blockIds = blockList.stream()
                .map(TimetableBlock::getTimetableBlockId)
                .toList();
        final Set<Long> likedBlockIds = (userId == null)
                ? Set.of()
                : new HashSet<>(timetableUserLikeRetriever.findLikedBlockIdsIn(userId, blockIds));

        final Map<Long, String> categoryColorMap = mapCategoryColors(categoryList);
        final List<TimetableResponse.Area> areaResponses = mapAreasToResponse(areaList);
        final List<TimetableResponse.Block> blockResponses = mapBlocksToResponse(blockList, categoryColorMap, likedBlockIds);

        return TimetableResponse.of(
                event.getName(),
                timetable.getStartAt(),
                timetable.getEndAt(),
                areaResponses,
                blockResponses
        );
    }

    @Transactional(readOnly = true)
    public TimetableDetailResponse getEventTimetableDetail(final long blockId, final Long userId) {
        final TimetableBlock timetableBlock;
        final TimetableCategory timetableCategory;
        final TimetableArea timetableArea;
        try {
            timetableBlock = timetableBlockRetriever.findTimetableBlockById(blockId);
            timetableCategory = timetableCategoryRetriever.findTimetableCategoryById(timetableBlock.getTimetableCategoryId());
            timetableArea = timetableAreaRetriever.findTimetableAreaById(timetableBlock.getTimetableAreaId());
        } catch (TimetableBlockNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_BLOCK);
        } catch (TimetableCategoryNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_CATEGORY);
        } catch (TimetableAreaNotFoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_AREA);
        }

        final boolean isUserLiked;
        if (userId == null) {
            isUserLiked = false;
        } else {
            isUserLiked = timetableUserLikeRetriever.isExistUserLikeByUserIdAndBlockId(userId, timetableBlock.getTimetableBlockId());
        }

        return TimetableDetailResponse.of(
                timetableBlock.getBlockName(),
                timetableCategory.getCategoryName(),
                timetableCategory.getCategoryColor(),
                isUserLiked,
                timetableBlock.getInformation(),
                timetableArea.getAreaName(),
                timetableBlock.getImageUrl(),
                timetableBlock.getBlockInfoRedirectUrl()
        );
    }

    private Map<Long, String> mapCategoryColors(final List<TimetableCategory> categoryList) {
        return categoryList.stream()
                .collect(Collectors.toMap(
                        TimetableCategory::getTimetableCategoryId,
                        TimetableCategory::getCategoryColor
                ));
    }

    private List<TimetableResponse.Area> mapAreasToResponse(final List<TimetableArea> areaList) {
        return areaList.stream()
                .sorted(Comparator.comparingInt(TimetableArea::getSequence))
                .map(area -> TimetableResponse.Area.of(
                        area.getTimetableAreaId(),
                        area.getAreaName(),
                        area.getSequence())
                ).toList();
    }

    private List<TimetableResponse.Block> mapBlocksToResponse(
            final List<TimetableBlock> blockList,
            final Map<Long, String> categoryColorMap,
            final Set<Long> likedBlockIds
    ) {
        return blockList.stream()
                .sorted(Comparator.comparing(TimetableBlock::getStartAt))
                .map(block -> {
                    final String categoryColor = Optional.ofNullable(
                                    categoryColorMap.get(block.getTimetableCategoryId()))
                            .orElseThrow(() -> new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_CATEGORY_COLOR));

                    final String encodedBlockId = secureUrlUtil.encode(block.getTimetableBlockId());

                    return TimetableResponse.Block.of(encodedBlockId,block.getBlockName(),
                            categoryColor,
                            block.getStartAt(),
                            block.getEndAt(),
                            block.getTimetableAreaId(),
                            likedBlockIds.contains(block.getTimetableBlockId())
                    );
                }).toList();
    }
}

