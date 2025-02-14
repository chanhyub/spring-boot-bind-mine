package com.example.springbootbindmine.modules.calendar.repository;

import com.example.springbootbindmine.modules.calendar.entity.CalendarEntity;
import com.example.springbootbindmine.modules.user.entity.UserEntity;
import com.example.springbootbindmine.modules.user.enums.Role;
import com.example.springbootbindmine.modules.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("CalendarRepository 테스트")
@DataJpaTest
@ActiveProfiles("test")
public class CalendarRepositoryTest {
    @Autowired
    private CalendarRepository calendarRepository;
    @Autowired
    private UserRepository userRepository;

    @DisplayName("CalendarEntity를 데이터베이스에 저장한다.")
    @Test
    void calendarSave(){
        // given
        UserEntity userEntity = createUser();
        UserEntity saveUserEntity = userRepository.save(userEntity);

        CalendarEntity calendarEntity = createCalendar(saveUserEntity);

        // when
        CalendarEntity saveCalendarEntity = calendarRepository.save(calendarEntity);

        // then
        assertEquals(saveCalendarEntity.getUser(), saveUserEntity);
        assertEquals(calendarEntity.getTitle(), saveCalendarEntity.getTitle());
        assertEquals(calendarEntity.getDescription(), saveCalendarEntity.getDescription());
        assertEquals(calendarEntity.getImageFileLink(), saveCalendarEntity.getImageFileLink());
        assertEquals(calendarEntity.getCreateDate(), saveCalendarEntity.getCreateDate());
    }

    @DisplayName("CalendarEntity를 데이터베이스에 저장하고 조회한다.")
    @Test
    void calendarSaveAndGet(){
        // given
        UserEntity userEntity = createUser();
        UserEntity saveUserEntity = userRepository.save(userEntity);

        CalendarEntity calendarEntity = createCalendar(saveUserEntity);
        CalendarEntity saveCalendarEntity = calendarRepository.save(calendarEntity);

        // when
        Optional<CalendarEntity> optionalFindCalendarEntity = calendarRepository.findById(saveCalendarEntity.getIdx());

        // then
        assertTrue(optionalFindCalendarEntity.isPresent());
        assertEquals(optionalFindCalendarEntity.get().getUser(), saveUserEntity);
        assertEquals(optionalFindCalendarEntity.get().getTitle(), saveCalendarEntity.getTitle());
        assertEquals(optionalFindCalendarEntity.get().getDescription(), saveCalendarEntity.getDescription());
        assertEquals(optionalFindCalendarEntity.get().getImageFileLink(), saveCalendarEntity.getImageFileLink());
        assertEquals(optionalFindCalendarEntity.get().getCreateDate(), saveCalendarEntity.getCreateDate());
    }

    @DisplayName("CalendarEntity를 데이터베이스에 저장하고 title을 수정한다.")
    @Test
    void calendarSaveAndUpdate(){
        // given
        UserEntity userEntity = createUser();
        UserEntity saveUserEntity = userRepository.save(userEntity);

        CalendarEntity calendarEntity = createCalendar(saveUserEntity);
        CalendarEntity saveCalendarEntity = calendarRepository.save(calendarEntity);

        // when
        String changeTitle = "수정된 타이틀";
        saveCalendarEntity.setTitle(changeTitle);
        CalendarEntity updateCalendarEntity = calendarRepository.save(saveCalendarEntity);

        // then
        assertEquals(updateCalendarEntity.getUser(), saveUserEntity);
        assertEquals(updateCalendarEntity.getTitle(), changeTitle);
        assertEquals(updateCalendarEntity.getDescription(), saveCalendarEntity.getDescription());
        assertEquals(updateCalendarEntity.getImageFileLink(), saveCalendarEntity.getImageFileLink());
        assertEquals(updateCalendarEntity.getCreateDate(), saveCalendarEntity.getCreateDate());
    }

    @DisplayName("CalendarEntity를 데이터베이스에 저장하고 삭제한다.")
    @Test
    void calendarSaveAndDelete(){
        // given
        UserEntity userEntity = createUser();
        UserEntity saveUserEntity = userRepository.save(userEntity);

        CalendarEntity calendarEntity = createCalendar(saveUserEntity);
        CalendarEntity saveCalendarEntity = calendarRepository.save(calendarEntity);

        // when
        calendarRepository.delete(saveCalendarEntity);

        // then
        assertTrue(calendarRepository.findById(saveCalendarEntity.getIdx()).isEmpty());
    }

    @DisplayName("CalendarEntity를 데이터베이스에 저장하고 UserEntity를 조건으로 전부 조회한다.")
    @Test
    void calendarFindAllByUserAndDeleteDateNull(){
        // given
        UserEntity userEntity = createUser();
        UserEntity saveUserEntity = userRepository.save(userEntity);

        CalendarEntity calendarEntity = createCalendar(saveUserEntity);
        CalendarEntity saveCalendarEntity = calendarRepository.save(calendarEntity);

        // when
        CalendarEntity findCalendarEntity = calendarRepository.findAllByUserAndDeleteDateNull(saveUserEntity).get(0);

        // then
        assertEquals(findCalendarEntity.getUser(), saveUserEntity);
        assertEquals(findCalendarEntity.getTitle(), saveCalendarEntity.getTitle());
        assertEquals(findCalendarEntity.getDescription(), saveCalendarEntity.getDescription());
        assertEquals(findCalendarEntity.getImageFileLink(), saveCalendarEntity.getImageFileLink());
        assertEquals(findCalendarEntity.getCreateDate(), saveCalendarEntity.getCreateDate());
    }

    @DisplayName("CalendarEntity를 데이터베이스에 저장하고 UserEntity를 조건으로 전부 조회할 때 나오지 않는 경우 빈 List를 리턴한다.")
    @Test
    void calendarFindAllByUserAndDeleteDateNullIsEmpty(){
        // given
        UserEntity userEntity = createUser();
        UserEntity saveUserEntity = userRepository.save(userEntity);

        // when
        List<CalendarEntity> findCalendarEntity = calendarRepository.findAllByUserAndDeleteDateNull(saveUserEntity);

        // then
        assertTrue(findCalendarEntity.isEmpty());
    }


    private CalendarEntity createCalendar(UserEntity userEntity) {
        return CalendarEntity.builder()
                .user(userEntity)
                .title("title")
                .description("content")
                .imageFileLink("http://localhost:8080/test.jpg")
                .createDate(LocalDateTime.now())
                .build();
    }

    private UserEntity createUser() {
        return UserEntity.builder()
                .name("테스트 유저")
                .email("test@test.com")
                .imageFileLink("http://localhost:8080/image.jpg")
                .userName("kakao_1234")
                .role(Role.USER)
                .createDate(LocalDateTime.now())
                .build();
    }
}
