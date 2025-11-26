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
public class AdminTimetableBlockRetriever {
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

    @Transactional(readOnly = true)
    public TimetableBlock findTimetableBlockById(final long timetableBlockId) {
        final TimetableBlockEntity timetableBlockEntity = timetableBlockRepository.findById(timetableBlockId).orElseThrow(TimetableBlockNotfoundException::new);
        return TimetableBlock.fromEntity(timetableBlockEntity);
    }

    @Transactional(readOnly = true)
    public void validExistTimetableBlock(final long timetableBlockId) {
        if (!timetableBlockRepository.existsById(timetableBlockId)) {
            throw new TimetableBlockNotfoundException();
        }
    }

    @Transactional(readOnly = true)
    public TimetableBlockEntity findTimetableBlockEntityByNotionTimetableBlockRowId(final String notionTimetableBlockRowId) {
        return timetableBlockRepository.findByNotionTimetableBlockRowId(notionTimetableBlockRowId).orElseThrow(TimetableBlockNotfoundException::new);
    }

    @Transactional(readOnly = true)
    public TimetableBlock findTimetableBlockByNotionTimetableBlockRowId(final String notionTimetableBlockRowId) {
        return TimetableBlock.fromEntity(timetableBlockRepository.findByNotionTimetableBlockRowId(notionTimetableBlockRowId).orElseThrow(TimetableBlockNotfoundException::new));
    }
}
