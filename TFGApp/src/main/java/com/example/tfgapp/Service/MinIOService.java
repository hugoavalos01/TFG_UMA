package com.example.tfgapp.Service;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import io.minio.messages.Item;
import okhttp3.MediaType;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Esta clase de servicio proporciona métodos para cargar y descargar archivos en el almacenamiento MinIO.
 */
@Service
public class MinIOService {

    @Autowired
    private MinioClient minioClient;

    /**
     * Descarga el archivo pasado como argumento del bucket especificado
     * @param bucketName
     * @param fileName
     * @return
     * @throws Exception
     */
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

    /**
     * Sube un archivo al bucket clasificado
     * @param fileName
     */
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

    /**
     * Elimina un archivo de un bucket
     * @param fileName
     * @throws Exception
     */
    public void deleteFile(String fileName) throws Exception {
        String bucketName = "sin-clasificar";
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }

    /**
     * Devuelve el nombre de los archivos en un bucket
     * @param bucketName
     * @return
     * @throws Exception
     */
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
     * @param fileName   El nombre que se le dará al archivo cargado.
     * @param file       El MultipartFile que contiene los datos del archivo a cargar.
     */
    public String uploadFile(String fileName, MultipartFile file) {
            try {
                // Comprobar si el archivo ya existe en el bucket "clasificado"
                minioClient.statObject(StatObjectArgs.builder()
                        .bucket("clasificado")
                        .object(fileName)
                        .build());

                // Si llegamos aquí, significa que el archivo ya existe, por lo que no se podrá clasificar luego
                return "El archivo ya está clasificado: " + fileName;
            } catch (ErrorResponseException e) {
                if (e.errorResponse().code().equals("NoSuchKey")) {
                    // Si el error es NoSuchKey, significa que el archivo no existe, y podemos continuar
                    System.out.println("El archivo no existe en el bucket 'clasificado'. Procediendo a subir.");
                } else {
                    // Si el error es otro, lanzar la excepción
                    e.printStackTrace();
                    System.out.println("Error al verificar el archivo en el bucket 'clasificado': " + e.getMessage());
                    return "Error al verificar el archivo en el bucket 'clasificado'" + e.getMessage();
                }
            } catch (MinioException e) {
                e.printStackTrace();
                System.out.println("Error al verificar el archivo en el bucket 'clasificado': " + e.getMessage());
                return "Error al verificar el archivo en el bucket 'clasificado'" + e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
                return "Error al verificar el archivo en el bucket 'clasificado'" + e.getMessage();
            }
        try {
            // Subir el archivo al bucket correspondiente
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket("sin-clasificar")
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            System.out.println("Archivo subido con éxito: " + fileName);
            return "El archivo se ha subido con éxito:" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al subir el archivo: " + e.getMessage());
            return "Error al subir el archivo: " + e.getMessage();
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
    public String getPresignedUrl(String bucketName, String objectName) throws MinioException {
        try {
            // Genera una URL prefirmada válida para descargar el objeto
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .method(Method.GET) // Especifica el método HTTP
                            .build()
            );
        } catch (Exception e) {
            throw new MinioException("Error al generar URL prefirmada para el objeto: " + e.getMessage());
        }
    }
}

