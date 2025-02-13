package com.example.msasbproducts.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    public List<String> submitFiles(List<MultipartFile> multipartFileList) throws IOException {
        List<String> imageUrlList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFileList) {
            // 파일명 지정 (겹치면 안 되고, 확장자도 포함)
            String fileName = UUID.randomUUID() + multipartFile.getOriginalFilename();
            // 파일을 S3에 업로드
            s3Service.upload(multipartFile, fileName);
            // DB에 저장할 파일명만 저장
            imageUrlList.add(fileName);
        }

        return imageUrlList;
    }
}
