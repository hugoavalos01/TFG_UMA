package com.example.tfgapp.Service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinIOService {

    @Autowired
    private MinioClient minioClient;

    public void uploadFile(String bucketName, String fileName, MultipartFile file) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            System.out.println("File uploaded successfully: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error uploading file: " + e.getMessage());
        }
    }

}
