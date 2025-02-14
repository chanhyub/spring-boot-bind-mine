package com.example.springbootbindmine.modules.calendar.response;

import com.example.springbootbindmine.modules.calendar.entity.CalendarEntity;

public record CalendarResponse(
        Long idx,
        String title,
        String description,
        String imageFileLink
) {
    public static CalendarResponse toDTO(CalendarEntity calendarEntity) {
        return new CalendarResponse(
                calendarEntity.getIdx(),
                calendarEntity.getTitle(),
                calendarEntity.getDescription(),
                calendarEntity.getImageFileLink()
        );

    }
}
