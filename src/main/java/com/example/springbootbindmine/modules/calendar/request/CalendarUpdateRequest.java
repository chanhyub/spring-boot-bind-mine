package com.example.springbootbindmine.modules.calendar.request;

public record CalendarUpdateRequest(
        Long idx,
        String title,
        String description,
        Boolean imageFileDelete
) {
}
