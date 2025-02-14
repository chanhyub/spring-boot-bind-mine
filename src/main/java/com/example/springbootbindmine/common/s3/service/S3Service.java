package com.example.springbootbindmine.common.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.example.springbootbindmine.common.exception.S3Exception;
import com.example.springbootbindmine.common.exception.error.S3ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }


    public String upload(MultipartFile image) {
        //입력받은 이미지 파일이 빈 파일인지 검증
        if(image.isEmpty()){
            throw new S3Exception(S3ErrorCode.EMPTY_FILE_UPLOAD);
        } else {
            image.getOriginalFilename();
        }
        //uploadImage를 호출하여 S3에 저장된 이미지의 public url을 반환한다.
        return uploadImageFile(image);
    }

    private String uploadImageFile(MultipartFile image) {
        validateImageFileExtension(image.getOriginalFilename());

        try {
            return uploadImageFileToS3(image);
        } catch (IOException e) {
            throw new S3Exception(S3ErrorCode.IMAGE_UPLOAD_FAIL);
        }
    }

    private void validateImageFileExtension(String originalFilename) {
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new S3Exception(S3ErrorCode.NO_FILE_EXTENSION);
        }

        String extention = originalFilename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png");

        if (!allowedExtentionList.contains(extention)) {
            throw new S3Exception(S3ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    private String uploadImageFileToS3(MultipartFile image) throws IOException {
        String originalFilename = image.getOriginalFilename(); //원본 파일 명

        String s3FileName = UUID.randomUUID().toString().substring(0, 10) + originalFilename; //변경된 파일 명

        InputStream is = image.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is); //image를 byte[]로 변환

        ObjectMetadata metadata = new ObjectMetadata(); //metadata 생성
        metadata.setContentType(image.getContentType());
        metadata.setContentLength(bytes.length);

        //S3에 요청할 때 사용할 byteInputStream 생성
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        try{
            //S3로 putObject 할 때 사용할 요청 객체
            //생성자 : bucket 이름, 파일 명, byteInputStream, metadata
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucket, s3FileName, byteArrayInputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);

            //실제로 S3에 이미지 데이터를 넣는 부분이다.
            amazonS3.putObject(putObjectRequest); // put image to S3
        }catch (Exception e){
            throw new S3Exception(S3ErrorCode.PUT_OBJECT_FAIL);
        }finally {
            byteArrayInputStream.close();
            is.close();
        }

        return amazonS3.getUrl(bucket, s3FileName).toString();
    }
}
