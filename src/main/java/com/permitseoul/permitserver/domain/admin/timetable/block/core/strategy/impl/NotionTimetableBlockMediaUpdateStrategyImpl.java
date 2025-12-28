package com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.impl;

import com.permitseoul.permitserver.domain.admin.timetable.base.core.exception.NotionPublicUrlNotFoundException;
import com.permitseoul.permitserver.domain.admin.timetable.base.core.exception.NotionUrlMalformedException;
import com.permitseoul.permitserver.domain.admin.timetable.block.api.dto.NotionTimetableBlockUpdateWebhookRequest;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.domain.NotionTimetableBlockWebhookType;
import com.permitseoul.permitserver.domain.admin.timetable.block.core.strategy.NotionTimetableBlockUpdateWebhookStrategy;
import com.permitseoul.permitserver.domain.admin.timetable.blockmedia.core.component.AdminTimetableBlockMediaSaver;
import com.permitseoul.permitserver.domain.admin.timetable.blockmedia.core.component.AdminTimetableBlockMediaRemover;
import com.permitseoul.permitserver.domain.admin.util.NotionImageUrlUtil;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.component.AdminTimetableBlockRetriever;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.TimetableBlock;
import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.domain.entity.TimetableBlockMediaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
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
        final String publicUrl = request.data().publicUrl();

        final String host;
        try {
            host = new java.net.URL(publicUrl).getHost();
        } catch (MalformedURLException e) {
            throw new NotionUrlMalformedException();
        }

        final TimetableBlock block = adminTimetableBlockRetriever.findTimetableBlockByNotionTimetableBlockRowId(rowId);
        final long timetableBlockId  = block.getTimetableBlockId();
        adminTimetableBlockMediaRemover.deleteAllByTimetableBlockId(timetableBlockId);

        final NotionTimetableBlockUpdateWebhookRequest.NotionFilesProperty mediaProp = request.data().properties().media();
        if (mediaProp == null || mediaProp.files() == null || mediaProp.files().isEmpty()) {
            return;
        }

        int sequence = 0;
        final List<TimetableBlockMediaEntity> medias = new ArrayList<>();

        for (NotionTimetableBlockUpdateWebhookRequest.NotionFileValue fileItem : mediaProp.files()) {
            if (fileItem == null || fileItem.file() == null) continue;

            final String original = fileItem.file().url();
            if (original == null || original.isBlank()) continue;

            final String proxyUrl = NotionImageUrlUtil.buildProxyUrl(host, rowId, original);
            if (proxyUrl == null || proxyUrl.isBlank()) continue;

            medias.add(TimetableBlockMediaEntity.create(timetableBlockId, sequence++, proxyUrl));
        }
        adminTimetableBlockMediaSaver.saveAllBlockMedia(medias);
    }
}
