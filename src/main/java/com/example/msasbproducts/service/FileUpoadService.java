package com.example.msasbproducts.service;

import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileUpoadService {

    @Autowired
    private S3Service s3Service;  // S3 서비스 객체

    // S3 클라이언트와 버킷 이름을 주입받아야 합니다.fdg
    @Autowired
    private AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> submitFiles(List<MultipartFile> multipartFileList) throws IOException {
        List<String> imageUrlList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFileList) {
            // 파일명 지정 (겹치면 안 되고, 확장자도 포함)
            String fileName = UUID.randomUUID() + multipartFile.getOriginalFilename();

            // S3에 업로드 (upload 메서드에 s3Client, bucket, objectKey, multipartFile 전달)
            String imageUrl = s3Service.upload(s3Client, bucket, fileName, multipartFile);

            // 이미지 URL을 DB에 저장할 목록에 추가
            imageUrlList.add(imageUrl);
        }

        return imageUrlList;
    }
}
