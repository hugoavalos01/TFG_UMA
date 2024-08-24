package com.example.tfgapp.Service;

import com.example.tfgapp.Utils.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Service
public class FileService {

    private final MinIOService minIOService;
    private final ImagenService imagenService;

        // Método para manejar la creación del archivo ZIP con la imagen y su información
        public byte[] generateZipWithImageAndInfo(String fileName) throws IOException {
            String bucketName = "clasificado";

            // Descargar la imagen desde MinIO
            try (InputStream fileInputStream = minIOService.downloadFile(bucketName, fileName)) {
                byte[] fileBytes = fileInputStream.readAllBytes();

                // Obtener informacion de la imagen
                String fileMetadata = imagenService.writeMetadataToFile(fileName);

                // Crear el archivo de texto
                byte[] textBytes = FileUtil.createTextFile(fileMetadata);

                // Crear el archivo ZIP con la imagen y la informacion
                return FileUtil.createZipFile(fileName, fileBytes, textBytes);
            }
        }
    }

