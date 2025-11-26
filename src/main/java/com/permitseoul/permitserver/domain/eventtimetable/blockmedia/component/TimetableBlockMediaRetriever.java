package com.permitseoul.permitserver.domain.eventtimetable.blockmedia.component;

import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.domain.TimetableBlockMedia;
import com.permitseoul.permitserver.domain.eventtimetable.blockmedia.repository.TimetableBlockMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TimetableBlockMediaRetriever {
    private final TimetableBlockMediaRepository mediaRepository;

    public List<TimetableBlockMedia> getAllTimetableBlockMediaByBlockId(final long blockId) {
        return mediaRepository.findAllByTimetableBlockIdOrderBySequenceAsc(blockId).stream().map(TimetableBlockMedia::fromEntity).toList();
    }

}
