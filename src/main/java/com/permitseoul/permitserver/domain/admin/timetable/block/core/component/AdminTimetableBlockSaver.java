package com.permitseoul.permitserver.domain.admin.timetable.block.core.component;

import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.TimetableBlock;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.domain.entity.TimetableBlockEntity;
import com.permitseoul.permitserver.domain.eventtimetable.block.core.repository.TimetableBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AdminTimetableBlockSaver {
    private final TimetableBlockRepository timetableBlockRepository;

    public List<TimetableBlock> saveAllTimetableBlocks(final List<TimetableBlockEntity> timetableBlockEntities) {
        return timetableBlockRepository.saveAll(timetableBlockEntities).stream()
                .map(TimetableBlock::fromEntity)
                .toList();
    }
}
