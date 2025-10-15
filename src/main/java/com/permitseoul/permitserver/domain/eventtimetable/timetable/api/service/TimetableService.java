package com.permitseoul.permitserver.domain.eventtimetable.timetable.api.service;

import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.component.TimetableStageRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.domain.TimetableStage;
import com.permitseoul.permitserver.domain.eventtimetable.stage.core.exception.TimetableStageNotFoundException;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.TimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.TimetableBlock;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.exception.TimetableBlockNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.component.TimetableBlockMediaRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.domain.TimetableBlockMedia;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.component.TimetableCategoryRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.domain.TimetableCategory;
import com.permitseoul.permitserver.domain.eventtimetable.category.core.exception.TimetableCategoryNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.dto.TimetableDetailResponse;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.dto.TimetableResponse;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.api.exception.NotfoundTimetableException;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.component.TimetableRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.Timetable;
import com.permitseoul.permitserver.domain.eventtimetable.timetable.core.domain.TimetableCategoryColor;
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
    private final TimetableStageRetriever timetableStageRetriever;
    private final TimetableCategoryRetriever timetableCategoryRetriever;
    private final TimetableBlockRetriever timetableBlockRetriever;
    private final TimetableBlockMediaRetriever timetableBlockMediaRetriever;
    private final TimetableUserLikeRetriever timetableUserLikeRetriever;
    private final EventRetriever eventRetriever;
    private final SecureUrlUtil secureUrlUtil;


    @Transactional(readOnly = true)
    public TimetableResponse getEventTimetable(final long eventId, final Long userId) {
        final Event event;
        final Timetable timetable;
        final List<TimetableStage> stageList;
        final List<TimetableCategory> categoryList;

        final List<TimetableBlock> blockList;
        try {
            event = eventRetriever.findEventById(eventId);
            timetable = timetableRetriever.getTimetableByEventId(eventId);
            final long timetableId = timetable.getTimetableId();
            stageList = timetableStageRetriever.findTimetableStageListByTimetableId(timetableId);
            categoryList = timetableCategoryRetriever.findAllTimetableCategory(timetableId);
            blockList = timetableBlockRetriever.findAllTimetableBlockByTimetableId(timetableId);

        } catch (EventNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_EVENT);
        } catch (TimetableNotFoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE);
        } catch (TimetableStageNotFoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_STAGE);
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

        final Map<String, TimetableCategoryColor> categoryColorMap = mapCategoryColors(categoryList);
        final List<TimetableResponse.Stage> stageResponse = mapStagesToResponse(stageList);
        final List<TimetableResponse.Block> blockResponses = mapBlocksToResponse(blockList, categoryColorMap, likedBlockIds);

        return TimetableResponse.of(
                event.getName(),
                timetable.getStartAt(),
                timetable.getEndAt(),
                stageResponse,
                blockResponses
        );
    }

    @Transactional(readOnly = true)
    public TimetableDetailResponse getEventTimetableDetail(final long blockId, final Long userId) {
        final TimetableBlock timetableBlock;
        final TimetableCategory timetableCategory;
        final TimetableStage timetableStage;
        final List<TimetableBlockMedia> timetableBlockMediaList;
        try {
            timetableBlock = timetableBlockRetriever.findTimetableBlockById(blockId);
            timetableBlockMediaList = timetableBlockMediaRetriever.getAllTimetableBlockMediaByBlockId(timetableBlock.getTimetableBlockId());
            timetableCategory = timetableCategoryRetriever.findTimetableCategoryByCategoryNotionRowId(timetableBlock.getTimetableCategoryNotionId());
            timetableStage = timetableStageRetriever.findTimetableStageByStageNotionRowId(timetableBlock.getTimetableStageNotionId());
        } catch (TimetableBlockNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_BLOCK);
        } catch (TimetableCategoryNotfoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_CATEGORY);
        } catch (TimetableStageNotFoundException e) {
            throw new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_STAGE);
        }

        final List<TimetableDetailResponse.MediaInfo> mediaInfos = sortTimetableBlockMedia(timetableBlockMediaList);
        final boolean isUserLiked = getUserLiked(userId, timetableBlock.getTimetableBlockId());

        return TimetableDetailResponse.of(
                timetableBlock.getBlockName(),
                timetableCategory.getCategoryName(),
                timetableCategory.getCategoryBackgroundColor(),
                timetableCategory.getCategoryLineColor(),
                isUserLiked,
                timetableBlock.getInformation(),
                timetableStage.getStageName(),
                timetableBlock.getBlockInfoRedirectUrl(),
                timetableBlock.getStartAt(),
                timetableBlock.getEndAt(),
                mediaInfos
        );
    }

    private boolean getUserLiked(final Long userId, final long timetableBlockId) {
        if (userId == null) {
            return false;
        } else {
            return timetableUserLikeRetriever.isExistUserLikeByUserIdAndBlockId(userId, timetableBlockId);
        }
    }

    private List<TimetableDetailResponse.MediaInfo> sortTimetableBlockMedia(final List<TimetableBlockMedia> timetableBlockMediaList) {
        return timetableBlockMediaList.stream()
                .sorted(Comparator.comparingInt(TimetableBlockMedia::getSequence))
                .map(media -> TimetableDetailResponse.MediaInfo.of(media.getMediaUrl()))
                .toList();
    }

    private Map<String, TimetableCategoryColor> mapCategoryColors(final List<TimetableCategory> categoryList) {
        return categoryList.stream()
                .collect(Collectors.toMap(
                        TimetableCategory::getCategoryNotionId,
                        category -> new TimetableCategoryColor(
                                category.getCategoryBackgroundColor(),
                                category.getCategoryLineColor()
                        )
                ));
    }

    private List<TimetableResponse.Stage> mapStagesToResponse(final List<TimetableStage> stageList) {
        return stageList.stream()
                .sorted(Comparator.comparingInt(TimetableStage::getSequence))
                .map(stage -> TimetableResponse.Stage.of(
                        stage.getStageNotionId(),
                        stage.getStageName(),
                        stage.getSequence())
                ).toList();
    }

    private List<TimetableResponse.Block> mapBlocksToResponse(
            final List<TimetableBlock> blockList,
            final Map<String, TimetableCategoryColor> categoryColorMap,
            final Set<Long> likedBlockIds
    ) {
        return blockList.stream()
                .sorted(Comparator.comparing(TimetableBlock::getStartAt))
                .map(block -> {
                    final TimetableCategoryColor categoryColor = Optional.ofNullable(
                                    categoryColorMap.get(block.getTimetableCategoryNotionId()))
                            .orElseThrow(() -> new NotfoundTimetableException(ErrorCode.NOT_FOUND_TIMETABLE_CATEGORY_COLOR));

                    final String encodedBlockId = secureUrlUtil.encode(block.getTimetableBlockId());

                    return TimetableResponse.Block.of(
                            encodedBlockId,
                            block.getBlockName(),
                            categoryColor.backgroundColor(),
                            categoryColor.lineColor(),
                            block.getStartAt(),
                            block.getEndAt(),
                            block.getTimetableStageNotionId(),
                            likedBlockIds.contains(block.getTimetableBlockId())
                    );
                }).toList();
    }
}

