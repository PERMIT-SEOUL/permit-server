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
}
