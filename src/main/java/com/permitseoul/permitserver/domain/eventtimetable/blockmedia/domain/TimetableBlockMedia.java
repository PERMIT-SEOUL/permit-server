package com.permitseoul.permitserver.domain.eventtimetable.blockmedia.domain;

import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.domain.entity.TimetableBlockMediaEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class TimetableBlockMedia {
    private final Long timetableBlockMediaId;
    private final long timetableBlockId;
    private final int sequence;
    private final String mediaUrl;

    public static TimetableBlockMedia fromEntity(final TimetableBlockMediaEntity timetableBlockMediaEntity) {
        return new TimetableBlockMedia(
                timetableBlockMediaEntity.getTimetableBlockMediaId(),
                timetableBlockMediaEntity.getTimetableBlockId(),
                timetableBlockMediaEntity.getSequence(),
                timetableBlockMediaEntity.getMediaUrl()
        );
    }
}
