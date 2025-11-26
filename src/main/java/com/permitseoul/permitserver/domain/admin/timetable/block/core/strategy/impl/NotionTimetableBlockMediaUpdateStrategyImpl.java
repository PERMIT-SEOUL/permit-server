package com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.impl;

import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.domain.NotionTimetableBlockWebhookType;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.NotionTimetableBlockUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.admin.timetable.blockmedia.core.component.AdminTimetableBlockMediaSaver;
import com.permitseoul.permitserver.domain.admin.timetable.blockmedia.core.component.AdminTimetableBlockMediaRemover;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.AdminTimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.TimetableBlock;
import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.domain.entity.TimetableBlockMediaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotionTimetableBlockMediaUpdateStrategyImpl implements NotionTimetableBlockUpdateWebhookStrategy {

    private final AdminTimetableBlockRetriever adminTimetableBlockRetriever;
    private final AdminTimetableBlockMediaRemover adminTimetableBlockMediaRemover;
    private final AdminTimetableBlockMediaSaver adminTimetableBlockMediaSaver;

    @Override
    public NotionTimetableBlockWebhookType getType() {
        return NotionTimetableBlockWebhookType.MEDIA;
    }

    @Override
    public void updateNotionTimetableBlockByNotionWebhook(final NotionTimetableBlockUpdateWebhookRequest request) {
        final String rowId = request.data().id();

        final TimetableBlock block = adminTimetableBlockRetriever.findTimetableBlockByNotionTimetableBlockRowId(rowId);
        final long timetableBlockId  = block.getTimetableBlockId();
        adminTimetableBlockMediaRemover.deleteAllByTimetableBlockId(timetableBlockId);

        final NotionTimetableBlockUpdateWebhookRequest.NotionFilesProperty mediaProp = request.data().properties().media();
        if (mediaProp == null || mediaProp.files() == null || mediaProp.files().isEmpty()) {
            return;
        }

        int sequence = 0;
        final List<TimetableBlockMediaEntity> medias = new ArrayList<>();
        for (NotionTimetableBlockUpdateWebhookRequest.NotionFileValue file : mediaProp.files()) {
            String url = null;
            if (file.file() != null && file.file().url() != null) {
                url = file.file().url();
            }
            if (url == null || url.isBlank()) {
                continue;
            }

            medias.add(TimetableBlockMediaEntity.create(timetableBlockId, sequence++, url));
        }
        adminTimetableBlockMediaSaver.saveAllBlockMedia(medias);
    }
}
