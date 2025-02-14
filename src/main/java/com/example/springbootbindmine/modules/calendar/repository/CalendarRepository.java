package com.example.springbootbindmine.modules.calendar.repository;

import com.example.springbootbindmine.modules.calendar.entity.CalendarEntity;
import com.example.springbootbindmine.modules.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CalendarRepository extends JpaRepository<CalendarEntity, Long> {
    public Page<CalendarEntity> findAllByUserAndDeleteDateNull(Pageable pageable, UserEntity userEntity);
    public List<CalendarEntity> findAllByUserAndDeleteDateNull(UserEntity userEntity);
}
