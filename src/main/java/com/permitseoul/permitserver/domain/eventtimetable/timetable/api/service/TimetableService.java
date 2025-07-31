package com.permitseoul.permitserver.domain.eventtimetable.timetable.api.service;

import com.permitseoul.permitserver.domain.eventtimetable.area.core.component.TimetableAreaRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.area.core.domain.TimetableArea;
import com.permitseoul.permitserver.domain.eventtimetable.area.core.exception.TimetableAreaNotFoundException;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.TimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.TimetableBlock;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.exception.TimetableBlockNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.component.TimetableCategoryRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.TimetableCategory;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.entity.TimetableCategoryEntity;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.exception.TimetableCategoryNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.dto.TimetableResponse;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.exception.NotfoundTimetableException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.component.TimetableRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.exception.TimetableNotFoundException;
import com.permitseoul.permitserver.global.response.code.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TimetableService {
    private final TimetableRetriever timetableRetriever;
    private final TimetableAreaRetriever timetableAreaRetriever;
    private final TimetableCategoryRetriever timetableCategoryRetriever;
    private final TimetableBlockRetriever timetableBlockRetriever;

    @Transactional(readOnly = true)
    public TimetableResponse getEventTimetable(final long eventId) {
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
            final List<TimetableResponse.Area> areaResponses = timetableAreaList.stream()
                    .sorted(Comparator.comparingInt(TimetableArea::getSequence))
                    .map(a -> new TimetableResponse.Area(a.getAreaName(), a.getSequence()))
                    .toList();
            final List<TimetableResponse.Block> blockResponses = timetableBlockList.stream()
                    .sorted(Comparator.comparing(TimetableBlock::getStartDate))
                    .map(b -> new TimetableResponse.Block(
                            String.valueOf(b.getTimetableBlockId()),
                            b.getBlockName(),
                            timetableCategoryColorMap.getOrDefault(b.getTimetableCategoryId(), "#000000"),
                            b.getStartDate(),
                            b.getEndDate(),
                            b.getInformation() // 또는 areaName이 따로 있다면 거기서 매핑
                    )).toList();

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
