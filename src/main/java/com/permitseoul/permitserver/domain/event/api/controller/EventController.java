package com.permitseoul.permitserver.domain.event.api.controller;


import com.permitseoul.permitserver.domain.event.api.service.EventService;
import com.permitseoul.permitserver.global.resolver.event.EventIdPathVariable;
import com.permitseoul.permitserver.global.response.ApiResponseUtil;
import com.permitseoul.permitserver.global.response.BaseResponse;
import com.permitseoul.permitserver.global.response.code.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.KeyValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    // 행사 전체 조회 api
    @GetMapping()
    public ResponseEntity<BaseResponse<?>> getAllEvents() {
        log.info("test",
              keyValue("testest", "testes")
        );
        return ApiResponseUtil.success(SuccessCode.OK, eventService.getAllVisibleEvents());
    }

    //행사 상세 정보 조회 api
    @GetMapping("detail/{eventId}")
    public ResponseEntity<BaseResponse<?>> getEventDetail(@EventIdPathVariable final Long eventId) {
        return ApiResponseUtil.success(SuccessCode.OK, eventService.getEventDetail(eventId));
    }
}
