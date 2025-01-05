package com.example.springbootbindmine.modules.calendar.repository;

import com.example.springbootbindmine.modules.calendar.entity.CalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<CalendarEntity, Long> {
}
