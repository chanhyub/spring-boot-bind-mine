package com.example.springbootbindmine.modules.calendar.controller;

import com.example.springbootbindmine.common.exception.RestApiException;
import com.example.springbootbindmine.common.exception.error.UserErrorCode;
import com.example.springbootbindmine.common.s3.service.S3Service;
import com.example.springbootbindmine.modules.calendar.request.CalendarSaveRequest;
import com.example.springbootbindmine.modules.calendar.request.CalendarUpdateRequest;
import com.example.springbootbindmine.modules.calendar.response.CalendarResponse;
import com.example.springbootbindmine.modules.calendar.service.CalendarService;
import com.example.springbootbindmine.modules.user.entity.UserEntity;
import com.example.springbootbindmine.modules.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/calendar")
public class CalendarController {
    private final CalendarService calendarService;
    private final UserService userService;
    private final S3Service s3Service;

    public CalendarController(
            CalendarService calendarService,
            UserService userService,
            S3Service s3Service
    ) {
        this.calendarService = calendarService;
        this.userService = userService;
        this.s3Service = s3Service;
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('USER')")
    public Page<CalendarResponse> getCalendarList(@RequestParam Integer page, @RequestParam Integer size) {
        Optional<UserEntity> optionalUserEntity = userService.getUserByAuthentication();
        optionalUserEntity.orElseThrow(() -> new RestApiException(UserErrorCode.INACTIVE_USER));

        return calendarService.getCalendarList(page, size, optionalUserEntity.get());
    }

    @PostMapping("/save")
    @PreAuthorize("hasRole('USER')")
    public CalendarResponse saveCalendar(@RequestPart(required = false) MultipartFile imageFile, @RequestPart CalendarSaveRequest calendarSaveRequest) {
        Optional<UserEntity> optionalUserEntity = userService.getUserByAuthentication();
        optionalUserEntity.orElseThrow(() -> new RestApiException(UserErrorCode.INACTIVE_USER));

        String imageFileUrl = null;

        if(imageFile != null){
            imageFileUrl = s3Service.upload(imageFile);
        }

        return calendarService.saveCalendar(imageFileUrl, calendarSaveRequest, optionalUserEntity.get());
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('USER')")
    public CalendarResponse updateCalendar(@RequestPart(required = false) MultipartFile imageFile, @RequestPart CalendarUpdateRequest calendarUpdateRequest) {
        String imageFileUrl = null;

        if(imageFile != null){
            imageFileUrl = s3Service.upload(imageFile);
        }

        return calendarService.updateCalendar(imageFileUrl, calendarUpdateRequest);
    }

    @DeleteMapping("/delete/{idx}")
    @PreAuthorize("hasRole('USER')")
    public void deleteCalendar(@PathVariable Long idx) {
        calendarService.deleteCalendar(idx);
    }

}
