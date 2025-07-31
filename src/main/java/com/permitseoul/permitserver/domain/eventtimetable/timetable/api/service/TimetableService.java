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
        try {
            final Timetable timetable = timetableRetriever.getTimetableByEventId(eventId);
            final List<TimetableArea> timetableAreaList = timetableAreaRetriever.findTimetableListByTimetableId(timetable.getTimetableId());
            final List<TimetableCategory> timetableCategoryList = timetableCategoryRetriever.findAllTimetableCategory(timetable.getTimetableId());
            final List<TimetableBlock> timetableBlockList = timetableBlockRetriever.findAllTimetableBlockByTimetableId(timetable.getTimetableId());

            final Map<Long, String> timetableCategoryColorMap = timetableCategoryList.stream()
                    .collect(Collectors.toMap(
                            TimetableCategory::getTimetableCategoryId,
                            TimetableCategory::getCategoryColor)
                    );

            final Set<Long> likedBlockIds = (userId == null)
                    ? Set.of()
                    :new HashSet<>(timetableUserLikeRetriever.findAllBlockIdsLikedByUserId(userId));

            final List<TimetableResponse.Area> areaResponses = timetableAreaList.stream()
                    .sorted(Comparator.comparingInt(TimetableArea::getSequence))
                    .map(area -> new TimetableResponse.Area(
                            area.getTimetableAreaId(),
                            area.getAreaName(),
                            area.getSequence())
                    ).toList();
            final List<TimetableResponse.Block> blockResponses = timetableBlockList.stream()
                    .sorted(Comparator.comparing(TimetableBlock::getStartDate))
                    .map(timetableBlock -> {
                        final String categoryColor = Optional.ofNullable(timetableCategoryColorMap.get(timetableBlock.getTimetableCategoryId()))
                                .orElseThrow(() -> new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_CATEGORY_COLOR));
                        final String encodedTimetableBlockId = secureUrlUtil.encode(timetableBlock.getTimetableBlockId());

                        return new TimetableResponse.Block(
                                encodedTimetableBlockId,
                                timetableBlock.getBlockName(),
                                categoryColor,
                                timetableBlock.getStartDate(),
                                timetableBlock.getEndDate(),
                                timetableBlock.getTimetableAreaId(),
                                likedBlockIds.contains(timetableBlock.getTimetableBlockId()));
                    }).toList();

            return new TimetableResponse(
                    timetable.getStartDate(),
                    timetable.getEndDate(),
                    areaResponses,
                    blockResponses
            );
        } catch (TimetableNotFoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE);
        } catch (TimetableAreaNotFoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_AREA);
        } catch (TimetableCategoryNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_CATEGORY);
        } catch (TimetableBlockNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_BLOCK);
        }
    }
}
