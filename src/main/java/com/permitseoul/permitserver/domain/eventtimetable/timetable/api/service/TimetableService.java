package com.permitseoul.permitserver.domain.eventtimetable.timetable.api.service;

import com.permitseoul.permitserver.domain.eventtimetable.area.core.component.TimetableAreaRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.area.core.domain.TimetableArea;
import com.permitseoul.permitserver.domain.eventtimetable.area.core.exception.TimetableAreaNotFoundException;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.TimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.TimetableBlock;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.exception.TimetableBlockNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.component.TimetableCategoryRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.TimetableCategory;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.exception.TimetableCategoryNotfoundException;
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
    private final SecureUrlUtil secureUrlUtil;

    @Transactional(readOnly = true)
    public TimetableResponse getEventTimetable(final long eventId, final Long userId) {
        final Timetable timetable = getTimetableByEventId(eventId);
        final List<TimetableArea> areaList = getAreaListByTimetableId(timetable.getTimetableId());
        final List<TimetableCategory> categoryList = getCategoryListByTimetableId(timetable.getTimetableId());
        final List<TimetableBlock> blockList = getBlockListByTimetableId(timetable.getTimetableId());

        final Map<Long, String> categoryColorMap = mapCategoryColors(categoryList);
        final Set<Long> likedBlockIds = getLikedBlockIdList(userId);

        final List<TimetableResponse.Area> areaResponses = mapAreasToResponse(areaList);
        final List<TimetableResponse.Block> blockResponses = mapBlocksToResponse(blockList, categoryColorMap, likedBlockIds);

        return new TimetableResponse(
                timetable.getStartDate(),
                timetable.getEndDate(),
                areaResponses,
                blockResponses
        );
    }

    private Timetable getTimetableByEventId(final long eventId) {
        try {
            return timetableRetriever.getTimetableByEventId(eventId);
        } catch (TimetableNotFoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE);
        }
    }

    private List<TimetableArea> getAreaListByTimetableId(final long timetableId) {
        try {
            return timetableAreaRetriever.findTimetableAreaListByTimetableId(timetableId);
        } catch (TimetableAreaNotFoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_AREA);
        }
    }

    private List<TimetableCategory> getCategoryListByTimetableId(final long timetableId) {
        try {
            return timetableCategoryRetriever.findAllTimetableCategory(timetableId);
        } catch (TimetableCategoryNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_CATEGORY);
        }
    }

    private List<TimetableBlock> getBlockListByTimetableId(final long timetableId) {
        try {
            return timetableBlockRetriever.findAllTimetableBlockByTimetableId(timetableId);
        } catch (TimetableBlockNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_BLOCK);
        }
    }

    private Map<Long, String> mapCategoryColors(final List<TimetableCategory> categoryList) {
        return categoryList.stream()
                .collect(Collectors.toMap(
                        TimetableCategory::getTimetableCategoryId,
                        TimetableCategory::getCategoryColor
                ));
    }

    private Set<Long> getLikedBlockIdList(final Long userId) {
        if (userId == null) return Set.of();
        return new HashSet<>(timetableUserLikeRetriever.findAllBlockIdsLikedByUserId(userId));
    }

    private List<TimetableResponse.Area> mapAreasToResponse(final List<TimetableArea> areaList) {
        return areaList.stream()
                .sorted(Comparator.comparingInt(TimetableArea::getSequence))
                .map(area -> new TimetableResponse.Area(
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
                .sorted(Comparator.comparing(TimetableBlock::getStartDate))
                .map(block -> {
                    final String categoryColor = Optional.ofNullable(
                                    categoryColorMap.get(block.getTimetableCategoryId()))
                            .orElseThrow(() -> new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_CATEGORY_COLOR));

                    final String encodedBlockId = secureUrlUtil.encode(block.getTimetableBlockId());

                    return new TimetableResponse.Block(
                            encodedBlockId,
                            block.getBlockName(),
                            categoryColor,
                            block.getStartDate(),
                            block.getEndDate(),
                            block.getTimetableAreaId(),
                            likedBlockIds.contains(block.getTimetableBlockId()));
                }).toList();
    }
}

