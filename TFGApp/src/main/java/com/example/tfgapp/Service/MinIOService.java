package com.example.tfgapp.Service;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase de servicio proporciona métodos para cargar y descargar archivos en el almacenamiento MinIO.
 */
@Service
public class MinIOService {

    @Autowired
    private MinioClient minioClient;

    public String downloadTempFile(String bucketName, String fileName) throws Exception {
        String filePath =  ".\\yolov5\\images\\" + fileName; // Ruta temporal para guardar el archivo
        minioClient.downloadObject(
                DownloadObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .filename(filePath)
                        .build()
        );
        return filePath;
    }
    public void uploadClassifiedFile(String fileName) {
        String bucketName = "clasificado";
        String filePath = ".\\yolov5\\results\\total\\"+fileName;
        File file = new File(filePath);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(fileInputStream, file.length(), -1)
                    .contentType("image/jpeg") // Cambia esto según el tipo de archivo
                    .build());
            System.out.println("Archivo subido con éxito: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al subir el archivo: " + e.getMessage());
        }
    }

    public void deleteFile(String fileName) throws Exception {
        String bucketName = "sin-clasificar";
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    public List<String> listFiles(String bucketName) throws Exception {
        List<String> fileNames = new ArrayList<>();

        Iterable<Result<Item>> objects = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
        for (Result<Item> result : objects) {
            Item item = result.get();
            fileNames.add(item.objectName());
        }

        return fileNames;
    }

    /**
     * Sube un archivo al bucket especificado en el almacenamiento MinIO.
     *
     * @param bucketName El nombre del bucket en el que se cargará el archivo.
     * @param fileName   El nombre que se le dará al archivo cargado.
     * @param file       El MultipartFile que contiene los datos del archivo a cargar.
     */
    public void uploadFile(String bucketName, String fileName, MultipartFile file) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            System.out.println("Archivo subido con éxito: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al subir el archivo: " + e.getMessage());
        }
    }

    /**
     * Descarga un archivo del bucket especificado en el almacenamiento MinIO.
     *
     * @param bucketName El nombre del bucket desde el que se descargará el archivo.
     * @param fileName   El nombre del archivo que se descargará.
     * @return Un InputStream que contiene el contenido del archivo descargado.
     * @throws IOException              Si ocurre un error de E/S durante la descarga del archivo.
     * @throws RuntimeException         Si ocurre un error durante el proceso de descarga.
     * @throws MinioException           Si ocurre un error al interactuar con MinIO.
     * @throws InvalidKeyException      Si la clave de acceso proporcionada es inválida.
     * @throws NoSuchAlgorithmException Si el algoritmo solicitado no está disponible.
     */
    public InputStream downloadFile(String bucketName, String fileName) throws IOException {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al descargar el archivo: " + e.getMessage());
        }
    }

    /**
     * Mueve la imagen de un bucket a otro
     * @param sourceBucket
     * @param destinationBucket
     * @param fileName
     * @throws IOException
     */
    public void moveImage(String fileName) throws IOException {
        String sourceBucket = "sin-clasificar";
        String destinationBucket = "clasificado";
        try {
            // Descargar la imagen del bucket de origen
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(sourceBucket)
                            .object(fileName)
                            .build()
            );

            // Subir la imagen al bucket de destino
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(destinationBucket)
                            .object(fileName)
                            .stream(inputStream, inputStream.available(), -1)
                            .build()
            );

            // Eliminar la imagen del bucket de origen
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(sourceBucket)
                            .object(fileName)
                            .build()
            );

            System.out.println("Archivo movido de " + sourceBucket + " a " + destinationBucket + ": " + fileName);
        } catch (MinioException | InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al mover la imagen: " + e.getMessage());
        }
    }
}

