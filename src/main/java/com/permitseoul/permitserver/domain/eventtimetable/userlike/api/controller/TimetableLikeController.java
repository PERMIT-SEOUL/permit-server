package com.permitseoul.permitserver.domain.eventtimetable.userlike.api.controller;

import com.permitseoul.permitserver.domain.eventtimetable.userlike.api.service.TimetableLikeService;
import com.permitseoul.permitserver.global.resolver.timetableblock.TimetableBlockIdPathVariable;
import com.permitseoul.permitserver.global.resolver.user.UserIdHeader;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import com.permitseoul.permitserver.global.util.SecureUrlUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events/timetables/likes")
public class TimetableLikeController {
    private final TimetableLikeService timetableLikeService;

    // 행사 타임테이블 좋아요 API
    @PostMapping("/{blockId}")
    public ResponseEntity<BaseResponse<?>> likeTimetable(
            @UserIdHeader final Long userId,
            @TimetableBlockIdPathVariable final Long blockId
    ) {
        timetableLikeService.likeBlock(userId, blockId);
        return ApiResponseUtil.success(SuccessCode.OK);
    }

    // 행사 타임테이블 좋아요 취소 API
    @DeleteMapping("/{blockId}")
    public ResponseEntity<BaseResponse<?>> disLikeTimetable(
            @UserIdHeader final Long userId,
            @TimetableBlockIdPathVariable final Long blockId
    ) {
        timetableLikeService.disLikeBlock(userId, blockId);
        return ApiResponseUtil.success(SuccessCode.OK);
    }


}
