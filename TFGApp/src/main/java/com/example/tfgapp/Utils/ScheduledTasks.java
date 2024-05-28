package com.example.tfgapp.Utils;

import com.example.tfgapp.Service.MinIOService;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduledTasks {

    private final MinIOService minIOService;
    private final MinioClient minioClient;

    public ScheduledTasks(MinIOService minIOService, MinioClient minioClient) {
        this.minIOService = minIOService;
        this.minioClient = minioClient;
    }

    @Scheduled(cron = "0 0 22 * * *") // Ejecutar a las 22:00 todos los días
    public void moveImagesScheduled() {
        try {
            List<String> imageNames = listAllImages("sin-clasificar");
            for (String imageName : imageNames) {
                minIOService.moveImage("sin-clasificar", "clasificado", imageName);
            }
        } catch (IOException | MinioException e) {
            e.printStackTrace();
            // Manejar el error
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> listAllImages(String bucketName) throws IOException, MinioException, NoSuchAlgorithmException, InvalidKeyException {
        List<String> imageNames = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
        for (Result<Item> result : results) {
            Item item = result.get();
            imageNames.add(item.objectName());
        }
        return imageNames;
    }
}
