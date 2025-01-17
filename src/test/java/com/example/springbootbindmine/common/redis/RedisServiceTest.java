package com.example.springbootbindmine.common.redis;

import com.example.springbootbindmine.common.redis.config.RedisConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RedisService 테스트")
@SpringBootTest
@ActiveProfiles("test")
public class RedisServiceTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void deleteAll() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @DisplayName("TTL 설정 없이 redis에 저장한다.")
    @Test
    void setValuesWithOutTTL() {
        // given
        String key = "kakao_1234";
        String data = "eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJ1c2VybmFtZSI6Imtha2FvXzM4NzEyNTM1MzMiLCJyb2xlIjoiVVNFUiIsImlhdCI6MTczNjQ0Njc5OCwiZXhwIjoxNzM2NTMzMTk4fQ.1tz_rG0j9n59S3Z7TxM8G4Lmk218bLFa0HKPyHIUhBE";

        // when
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);

        // then
        assertThat(values.get(key)).isEqualTo(data);
    }

    @DisplayName("TTL 설정 하며 redis에 저장한다.")
    @Test
    void setValuesWithTTL() {
        // given
        String key = "kakao_1234";
        String data = "eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJ1c2VybmFtZSI6Imtha2FvXzM4NzEyNTM1MzMiLCJyb2xlIjoiVVNFUiIsImlhdCI6MTczNjQ0Njc5OCwiZXhwIjoxNzM2NTMzMTk4fQ.1tz_rG0j9n59S3Z7TxM8G4Lmk218bLFa0HKPyHIUhBE";
        Duration duration = Duration.ofMillis(259200000L);

        // when
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration);

        // then
        Assertions.assertEquals(values.getAndExpire(key, duration), data);
    }

    @DisplayName("redis에 저장된 데이터를 삭제한다.")
    @Test
    void deleteValues() {
        // given
        String key = "kakao_1234";
        String data = "eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJ1c2VybmFtZSI6Imtha2FvXzM4NzEyNTM1MzMiLCJyb2xlIjoiVVNFUiIsImlhdCI6MTczNjQ0Njc5OCwiZXhwIjoxNzM2NTMzMTk4fQ.1tz_rG0j9n59S3Z7TxM8G4Lmk218bLFa0HKPyHIUhBE";

        // when
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);
        redisTemplate.delete(key);

        // then
        assertThat(values.get(key)).isNull();
    }

    @DisplayName("redis에 저장된 데이터를 조회한다.")
    @Test
    void getValues() {
        // given
        String key = "kakao_1234";
        String data = "eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJ1c2VybmFtZSI6Imtha2FvXzM4NzEyNTM1MzMiLCJyb2xlIjoiVVNFUiIsImlhdCI6MTczNjQ0Njc5OCwiZXhwIjoxNzM2NTMzMTk4fQ.1tz_rG0j9n59S3Z7TxM8G4Lmk218bLFa0HKPyHIUhBE";

        // when
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);

        // then
        assertThat(values.get(key)).isEqualTo(data);
    }

    @DisplayName("redis에 저장된 데이터가 존재하는지 확인한다.")
    @Test
    void checkExistsValue() {
        // given
        String key = "kakao_1234";
        String data = "eyJhbGciOiJIUzI1NiJ9.eyJjYXRlZ29yeSI6InJlZnJlc2giLCJ1c2VybmFtZSI6Imtha2FvXzM4NzEyNTM1MzMiLCJyb2xlIjoiVVNFUiIsImlhdCI6MTczNjQ0Njc5OCwiZXhwIjoxNzM2NTMzMTk4fQ.1tz_rG0j9n59S3Z7TxM8G4Lmk218bLFa0HKPyHIUhBE";
        Boolean result;

        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);

        // when
        values.get("존재하지 않는 key");
        result = false;

        // then
        assertThat(result).isFalse();
    }
}
