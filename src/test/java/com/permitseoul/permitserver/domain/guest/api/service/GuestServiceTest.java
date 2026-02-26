package com.permitseoul.permitserver.domain.guest.api.service;

import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.GuestTicketStatus;
import com.permitseoul.permitserver.domain.admin.guestticket.core.domain.entity.GuestTicketEntity;
import com.permitseoul.permitserver.domain.admin.guestticket.core.exception.GuestTicketNotFoundException;
import com.permitseoul.permitserver.domain.event.core.component.EventRetriever;
import com.permitseoul.permitserver.domain.event.core.domain.Event;
import com.permitseoul.permitserver.domain.event.core.domain.EventType;
import com.permitseoul.permitserver.domain.event.core.exception.EventNotfoundException;
import com.permitseoul.permitserver.domain.guest.api.dto.res.GuestTicketValidateResponse;
import com.permitseoul.permitserver.domain.guest.api.exception.GuestNotFoundException;
import com.permitseoul.permitserver.domain.guest.api.exception.GuestTicketIllegalException;
import com.permitseoul.permitserver.domain.guest.core.component.GuestRetriever;
import com.permitseoul.permitserver.domain.guest.core.component.GuestUpdater;
import com.permitseoul.permitserver.domain.guest.core.domain.GuestTicket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GuestService 테스트")
class GuestServiceTest {

    @Mock
    private GuestRetriever guestRetriever;
    @Mock
    private EventRetriever eventRetriever;
    @Mock
    private GuestUpdater guestUpdater;
    @InjectMocks
    private GuestService guestService;

    private static final long EVENT_ID = 100L;
    private static final String TICKET_CODE = "GUEST-TICKET-001";
    private static final String CHECK_CODE = "EVENT-CHECK-CODE";
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 2, 18, 14, 0);

    private Event createEvent() {
        return new Event(EVENT_ID, "테스트 이벤트", EventType.PERMIT, NOW.minusDays(1), NOW.plusDays(1),
                "서울", "라인업", "상세", 0, NOW.minusDays(7), NOW.plusDays(7), CHECK_CODE);
    }

    private GuestTicket createGuestTicket(GuestTicketStatus status) {
        return new GuestTicket(1L, EVENT_ID, 10L, TICKET_CODE, status, null);
    }

    private GuestTicketEntity createGuestTicketEntity(GuestTicketStatus status) {
        final GuestTicketEntity entity = GuestTicketEntity.create(EVENT_ID, 10L, TICKET_CODE);
        ReflectionTestUtils.setField(entity, "status", status);
        return entity;
    }

    @Nested
    @DisplayName("validateGuestTicket")
    class ValidateGuestTicketTest {

        @Test
        @DisplayName("정상: 유효한 게스트 티켓 검증 → 이벤트 이름 반환")
        void success() {
            when(guestRetriever.findGuestTicketByTicketCode(TICKET_CODE))
                    .thenReturn(createGuestTicket(GuestTicketStatus.ISSUED));
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(createEvent());

            final GuestTicketValidateResponse result = guestService.validateGuestTicket(TICKET_CODE);

            assertThat(result.eventName()).isEqualTo("테스트 이벤트");
        }

        @Test
        @DisplayName("예외: 게스트 티켓 미존재 → GuestNotFoundException")
        void throwsWhenNotFound() {
            when(guestRetriever.findGuestTicketByTicketCode(TICKET_CODE)).thenThrow(new GuestTicketNotFoundException());

            assertThatThrownBy(() -> guestService.validateGuestTicket(TICKET_CODE))
                    .isInstanceOf(GuestNotFoundException.class);
        }

        @Test
        @DisplayName("예외: 이미 사용된 티켓 → GuestTicketIllegalException")
        void throwsWhenAlreadyUsed() {
            when(guestRetriever.findGuestTicketByTicketCode(TICKET_CODE))
                    .thenReturn(createGuestTicket(GuestTicketStatus.USED));

            assertThatThrownBy(() -> guestService.validateGuestTicket(TICKET_CODE))
                    .isInstanceOf(GuestTicketIllegalException.class);
        }
    }

    @Nested
    @DisplayName("confirmGuestTicketByStaffCheckCode")
    class ConfirmByCheckCodeTest {

        @Test
        @DisplayName("정상: 체크코드로 게스트 티켓 확인 → USED 상태로 변경")
        void success() {
            final GuestTicketEntity entity = createGuestTicketEntity(GuestTicketStatus.ISSUED);
            when(guestRetriever.findGuestTicketEntityByTicketCode(TICKET_CODE)).thenReturn(entity);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(createEvent());

            guestService.confirmGuestTicketByStaffCheckCode(TICKET_CODE, CHECK_CODE);

            verify(guestUpdater).updateGuestTicketStatus(entity, GuestTicketStatus.USED);
        }

        @Test
        @DisplayName("예외: 게스트 티켓 미존재 → GuestNotFoundException")
        void throwsWhenNotFound() {
            when(guestRetriever.findGuestTicketEntityByTicketCode(TICKET_CODE))
                    .thenThrow(new GuestTicketNotFoundException());

            assertThatThrownBy(() -> guestService.confirmGuestTicketByStaffCheckCode(TICKET_CODE, CHECK_CODE))
                    .isInstanceOf(GuestNotFoundException.class);
        }

        @Test
        @DisplayName("예외: 이미 사용된 티켓 → GuestTicketIllegalException")
        void throwsWhenAlreadyUsed() {
            final GuestTicketEntity entity = createGuestTicketEntity(GuestTicketStatus.USED);
            when(guestRetriever.findGuestTicketEntityByTicketCode(TICKET_CODE)).thenReturn(entity);

            assertThatThrownBy(() -> guestService.confirmGuestTicketByStaffCheckCode(TICKET_CODE, CHECK_CODE))
                    .isInstanceOf(GuestTicketIllegalException.class);
        }

        @Test
        @DisplayName("예외: 체크코드 불일치 → GuestTicketIllegalException")
        void throwsWhenCheckCodeMismatch() {
            final GuestTicketEntity entity = createGuestTicketEntity(GuestTicketStatus.ISSUED);
            when(guestRetriever.findGuestTicketEntityByTicketCode(TICKET_CODE)).thenReturn(entity);
            when(eventRetriever.findEventById(EVENT_ID)).thenReturn(createEvent());

            assertThatThrownBy(() -> guestService.confirmGuestTicketByStaffCheckCode(TICKET_CODE, "WRONG-CODE"))
                    .isInstanceOf(GuestTicketIllegalException.class);
        }
    }

    @Nested
    @DisplayName("confirmGuestTicketByStaffCamera")
    class ConfirmByCameraTest {

        @Test
        @DisplayName("정상: 카메라로 게스트 티켓 확인 → USED 상태로 변경")
        void success() {
            final GuestTicketEntity entity = createGuestTicketEntity(GuestTicketStatus.ISSUED);
            when(guestRetriever.findGuestTicketEntityByTicketCode(TICKET_CODE)).thenReturn(entity);

            guestService.confirmGuestTicketByStaffCamera(TICKET_CODE);

            verify(guestUpdater).updateGuestTicketStatus(entity, GuestTicketStatus.USED);
        }

        @Test
        @DisplayName("예외: 게스트 티켓 미존재 → GuestNotFoundException")
        void throwsWhenNotFound() {
            when(guestRetriever.findGuestTicketEntityByTicketCode(TICKET_CODE))
                    .thenThrow(new GuestTicketNotFoundException());

            assertThatThrownBy(() -> guestService.confirmGuestTicketByStaffCamera(TICKET_CODE))
                    .isInstanceOf(GuestNotFoundException.class);
        }
    }
}
