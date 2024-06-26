package com.example.tfgapp.Utils;

import com.example.tfgapp.Service.ImagenService;
import com.example.tfgapp.Service.MinIOService;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import org.springframework.scheduling.annotation.Async;
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
    private final ImagenService imagenService;
    private boolean clasificacionCompletada = false;

    public ScheduledTasks(MinIOService minIOService, MinioClient minioClient, ImagenService imagenService) {
        this.minIOService = minIOService;
        this.minioClient = minioClient;
        this.imagenService = imagenService;
    }

    @Async
    @Scheduled(cron = "0 0 22 * * *") // Ejecutar a las 22:00 todos los días
    public void clasificarImagenesScheduled() {
        clasificacionCompletada = false;
        try {
            imagenService.clasificarImagenes();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clasificacionCompletada = true;
        }
    }

    public boolean isClasificacionCompletada() {
        return clasificacionCompletada;
    }
}
