package com.example.msasbproducts.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
// 수정정
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    private AmazonS3 amazonS3;

    /* 1. 파일 업로드 */
    public String upload(AmazonS3 s3Client, String bucket, String objectKey, MultipartFile multipartFile) throws IOException {
        // S3 기본 URL 설정
        final String BASE_S3_URL = "https://products-bucket1.s3.ap-northeast-3.amazonaws.com/";

        // 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType()); // 파일 타입 설정

        // S3에 업로드
        s3Client.putObject(bucket, objectKey, multipartFile.getInputStream(), metadata);

        // 최종 URL 반환 (BASE_S3_URL이 자동으로 붙도록 설정)
        return formatS3Url(objectKey);
    }
    public String formatS3Url(String objectKey) {
        if (objectKey == null || objectKey.isEmpty()) {
            return null;
        }
        final String BASE_S3_URL = "https://products-bucket1.s3.ap-northeast-3.amazonaws.com/";
        return BASE_S3_URL + URLEncoder.encode(objectKey, StandardCharsets.UTF_8);
    }

    /* 2. 파일 삭제 */
    public void delete (String keyName) {
        try {
            // deleteObject(버킷명, 키값)으로 객체 삭제
            amazonS3.deleteObject(bucket, keyName);
        } catch (AmazonServiceException e) {
            log.error(e.toString());
        }
    }

    /* 3. 파일의 presigned URL 반환 */
    public String getPresignedURL (String keyName) {
        String preSignedURL = "";
        // presigned URL이 유효하게 동작할 만료기한 설정 (2분)
        Date expiration = new Date();
        Long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 2;
        expiration.setTime(expTimeMillis);

        try {
            // presigned URL 발급
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, keyName)
                    .withMethod(HttpMethod.GET)
                    .withExpiration(expiration);
            URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
            preSignedURL = url.toString();
        } catch (Exception e) {
            log.error(e.toString());
        }

        return preSignedURL;
    }
}
