package com.permitseoul.permitserver.domain.eventtimetable.userlike.api.controller;

import com.permitseoul.permitserver.domain.eventtimetable.userlike.api.service.TimetableLikeService;
import com.permitseoul.permitserver.global.resolver.timetableblock.TimetableBlockIdPathVariable;
import com.permitseoul.permitserver.global.resolver.user.UserIdHeader;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events/timetables")
public class TimetableLikeController {
    private final TimetableLikeService timetableLikeService;

    // 행사 타임테이블 좋아요 API
    @PostMapping("/{blockId}")
    public ResponseEntity<BaseResponse<?>> getEventTimetable(
            @UserIdHeader final Long userId,
            @TimetableBlockIdPathVariable final Long blockId
    ) {
        timetableLikeService.likeBlock(userId, blockId);
        return ApiResponseUtil.success(SuccessCode.OK);
    }
}
