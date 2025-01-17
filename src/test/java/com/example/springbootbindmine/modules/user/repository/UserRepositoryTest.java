package com.example.springbootbindmine.modules.user.repository;

import com.example.springbootbindmine.modules.user.entity.UserEntity;
import com.example.springbootbindmine.modules.user.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("UserRepository 테스트")
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @DisplayName("UserEntity를 데이터베이스에 저장한다.")
    @Test
    void save() {
        // given
        UserEntity userEntity = createUser();

        // when
        UserEntity saveUserEntity = userRepository.save(userEntity);

        // then
        assertEquals(userEntity.getName(), saveUserEntity.getName());
        assertEquals(userEntity.getEmail(), saveUserEntity.getEmail());
        assertEquals(userEntity.getImageFileLink(), saveUserEntity.getImageFileLink());
        assertEquals(userEntity.getUserName(), saveUserEntity.getUserName());
        assertEquals(userEntity.getRole(), saveUserEntity.getRole());
    }

    @DisplayName("UserEntity를 데이터베이스에 저장하고 조회한다.")
    @Test
    void saveAndGet() {
        // given
        UserEntity userEntity = createUser();
        UserEntity saveUserEntity = userRepository.save(userEntity);

        // when
        Optional<UserEntity> findUserEntity = userRepository.findById(saveUserEntity.getIdx());

        // then
        assertTrue(findUserEntity.isPresent());
        assertEquals(userEntity.getName(), findUserEntity.get().getName());
        assertEquals(userEntity.getEmail(), findUserEntity.get().getEmail());
        assertEquals(userEntity.getImageFileLink(), findUserEntity.get().getImageFileLink());
        assertEquals(userEntity.getUserName(), findUserEntity.get().getUserName());
        assertEquals(userEntity.getRole(), findUserEntity.get().getRole());
    }

    @DisplayName("UserEntity를 데이터베이스에 저장하고 name을 수정한다.")
    @Test
    void saveAndUpdate() {
        // given
        UserEntity userEntity = createUser();
        UserEntity saveUserEntity = userRepository.save(userEntity);

        // when
        String changeName = "테스트 유저2";
        saveUserEntity.setName(changeName);
        UserEntity updateUserEntity = userRepository.save(saveUserEntity);

        // then
        assertEquals(changeName, updateUserEntity.getName());
    }

    @DisplayName("UserEntity를 데이터베이스에 저장하고 삭제한다.")
    @Test
    void saveAndDelete() {
        // given
        UserEntity userEntity = createUser();
        UserEntity saveUserEntity = userRepository.save(userEntity);

        // when
        userRepository.delete(saveUserEntity);

        // then
        assertTrue(userRepository.findById(saveUserEntity.getIdx()).isEmpty());
    }

    @DisplayName("UserEntity를 데이터베이스에 저장하고 userName으로 조회한다.")
    @Test
    void findByUserName() {
        // given
        UserEntity userEntity = createUser();
        UserEntity saveUserEntity = userRepository.save(userEntity);

        // when
        Optional<UserEntity> findUserEntity = userRepository.findByUserName(saveUserEntity.getUserName());

        // then
        assertTrue(findUserEntity.isPresent());
        assertEquals(userEntity.getName(), findUserEntity.get().getName());
        assertEquals(userEntity.getEmail(), findUserEntity.get().getEmail());
        assertEquals(userEntity.getImageFileLink(), findUserEntity.get().getImageFileLink());
        assertEquals(userEntity.getUserName(), findUserEntity.get().getUserName());
        assertEquals(userEntity.getRole(), findUserEntity.get().getRole());
    }

    @DisplayName("UserName으로 조회 할 때 존재하지 않는 경우 null을 반환한다.")
    @Test
    void findByUserNameNotFound() {
        // given
        UserEntity userEntity = createUser();
        UserEntity saveUserEntity = userRepository.save(userEntity);

        // when
        Optional<UserEntity> findUserEntity = userRepository.findByUserName("notFound");

        // then
        assertTrue(findUserEntity.isEmpty());
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
