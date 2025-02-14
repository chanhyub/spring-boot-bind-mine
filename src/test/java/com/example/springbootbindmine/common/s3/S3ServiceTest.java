package com.example.springbootbindmine.common.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.example.springbootbindmine.common.s3.service.S3Service;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

@DisplayName("S3 테스트")
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class S3ServiceTest {
    @Mock
    private S3Service s3Service;

    @DisplayName("이미지 파일을 업로드 한다.")
    @Test
    void uploadImage() {
        // given
        MockMultipartFile imageFile = new MockMultipartFile(
                "imageFile",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "test".getBytes()
        );
        String imageFileUrl = "https://example.com/test.png";

        // when
        Mockito.when(s3Service.upload(imageFile)).thenReturn(imageFileUrl);

        // then
        Assertions.assertEquals(imageFileUrl, s3Service.upload(imageFile));
    }
}
