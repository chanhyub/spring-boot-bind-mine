package com.example.springbootbindmine.modules.calendar.service;

import com.example.springbootbindmine.common.exception.RestApiException;
import com.example.springbootbindmine.common.exception.error.CommonErrorCode;
import com.example.springbootbindmine.modules.calendar.entity.CalendarEntity;
import com.example.springbootbindmine.modules.calendar.repository.CalendarRepository;
import com.example.springbootbindmine.modules.calendar.request.CalendarSaveRequest;
import com.example.springbootbindmine.modules.calendar.request.CalendarUpdateRequest;
import com.example.springbootbindmine.modules.calendar.response.CalendarResponse;
import com.example.springbootbindmine.modules.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {
    private final CalendarRepository calendarRepository;

    public Page<CalendarResponse> getCalendarList(Integer page, Integer size, UserEntity userEntity) {
        Sort sort = Sort.by(Sort.Direction.ASC, "idx");
        Pageable pageable = PageRequest.of(page, size, sort);

        return calendarRepository.findAllByUserAndDeleteDateNull(pageable, userEntity).map(CalendarResponse::toDTO);
    }

    public CalendarResponse saveCalendar(String imageFileUrl, CalendarSaveRequest calendarSaveRequest, UserEntity userEntity) {
        CalendarEntity calendarEntity = CalendarEntity.builder()
                .user(userEntity)
                .title(calendarSaveRequest.title())
                .description(calendarSaveRequest.description())
                .imageFileLink(imageFileUrl)
                .createDate(LocalDateTime.now())
                .build();

        return CalendarResponse.toDTO(calendarRepository.save(calendarEntity));
    }

    public CalendarResponse updateCalendar(String imageFileUrl, CalendarUpdateRequest calendarUpdateRequest) {
        Optional<CalendarEntity> optionalCalendarEntity = calendarRepository.findById(calendarUpdateRequest.idx());
        CalendarEntity calendarEntity = optionalCalendarEntity.orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        if(imageFileUrl != null && !calendarUpdateRequest.imageFileDelete()){
            calendarEntity.setImageFileLink(imageFileUrl);
        }

        if(calendarUpdateRequest.imageFileDelete()){
            calendarEntity.setImageFileLink(null);
        }

        calendarEntity.setTitle(calendarUpdateRequest.title());
        calendarEntity.setDescription(calendarUpdateRequest.description());

        return CalendarResponse.toDTO(calendarRepository.save(calendarEntity));

    }

    public void deleteCalendar(Long idx) {
        Optional<CalendarEntity> optionalCalendarEntity = calendarRepository.findById(idx);
        CalendarEntity calendarEntity = optionalCalendarEntity.orElseThrow(() -> new RestApiException(CommonErrorCode.RESOURCE_NOT_FOUND));

        calendarEntity.setDeleteDate(LocalDateTime.now());
        calendarRepository.save(calendarEntity);
    }

}
