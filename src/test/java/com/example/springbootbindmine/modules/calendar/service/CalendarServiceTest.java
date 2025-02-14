package com.example.springbootbindmine.modules.calendar.service;

import com.example.springbootbindmine.modules.calendar.repository.CalendarRepository;
import com.example.springbootbindmine.modules.calendar.request.CalendarUpdateRequest;
import com.example.springbootbindmine.modules.calendar.response.CalendarResponse;
import com.example.springbootbindmine.modules.user.entity.UserEntity;
import com.example.springbootbindmine.modules.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DisplayName("CalendarService 테스트")
@SpringBootTest
@ActiveProfiles("test")
public class CalendarServiceTest {
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private CalendarRepository calendarRepository;
    @Autowired
    private UserRepository userRepository;

    @DisplayName("캘린더 리스트 조회를 시도 할 경우 로그인 한 사용자의 캘린더 리스트 전체를 페이징 처리하여 리턴한다.")
    @WithMockUser
    @Test
    void getCalendarList() {
        // given
        UserEntity userEntity = userRepository.findById(1L).get();
        Integer page = 0;
        Integer size = 10;

        // when
        Page<CalendarResponse> calendarResponseDTOPage = calendarService.getCalendarList(page, size, userEntity);

        // then
        assertFalse(calendarResponseDTOPage.isEmpty());
        assertEquals(calendarResponseDTOPage.getContent().get(0).title(), "테스트 캘린더1");
        assertEquals(calendarResponseDTOPage.getContent().get(0).description(), "테스트 캘린더 입니다.");
        assertEquals(calendarResponseDTOPage.getContent().get(0).imageFileLink(), "https://example.com/image1.jpg");
    }

    @DisplayName("캘린더 수정 정보를 입력받아 캘린더의 정보를 수정하고 수정된 캘린더 정보를 리턴한다.")
    @WithMockUser
    @Test
    void updateCalendar() {
        // given
        CalendarUpdateRequest calendarUpdateRequest = new CalendarUpdateRequest(1L, "수정된 캘린더1", "수정된 캘린더 입니다.", false);
        String imageFileUrl = "https://example.com/image1.jpg";

        // when
        CalendarResponse calendarResponseDTO = calendarService.updateCalendar(imageFileUrl, calendarUpdateRequest);

        // then
        assertEquals(calendarResponseDTO.title(), calendarUpdateRequest.title());
        assertEquals(calendarResponseDTO.description(), calendarUpdateRequest.description());
        assertEquals(calendarResponseDTO.imageFileLink(), imageFileUrl);
    }
}
