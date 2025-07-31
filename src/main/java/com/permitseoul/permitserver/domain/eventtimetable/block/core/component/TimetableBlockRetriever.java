package com.permitseoul.permitserver.domain.eventtimetable.block.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.TimetableBlock;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.exception.TimetableBlockNotfoundException;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.repository.TimetableBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TimetableBlockRetriever {
    private final TimetableBlockRepository timetableBlockRepository;

    @Transactional(readOnly = true)
    public List<TimetableBlock> findAllTimetableBlockByTimetableId(final long timetableId) {
        final List<TimetableBlockEntity> timetableBlockEntityList = timetableBlockRepository.findAllByTimetableId(timetableId);
        if (timetableBlockEntityList.isEmpty()) {
            throw new TimetableBlockNotfoundException();
        }
        return timetableBlockEntityList.stream()
                .map(TimetableBlock::fromEntity)
                .toList();
    }
}
