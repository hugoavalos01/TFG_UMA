package com.example.tfgapp.Controller;

import com.example.tfgapp.Entity.Imagen;
import com.example.tfgapp.Service.MinIOService;
import com.example.tfgapp.Service.ImagenService;
import com.example.tfgapp.Utils.ScheduledTasks;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.tfgapp.Utils.FileUtil.createTextFile;
import static com.example.tfgapp.Utils.FileUtil.createZipFile;

/**
 * Controlador para la gestión de archivos en la aplicación.
 */
@CrossOrigin("*")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Controller {

    private final ImagenService imagenService;
    private final MinIOService minioService;
    private final ScheduledTasks scheduledTasks;

    // Mapa que mapea extensiones de archivo a tipos de archivos.
    private static final Map<String, MediaType> MEDIA_TYPE_MAP = new HashMap<>();

    static {
        MEDIA_TYPE_MAP.put("jpg", MediaType.IMAGE_JPEG);
        MEDIA_TYPE_MAP.put("jpeg", MediaType.IMAGE_JPEG);
        MEDIA_TYPE_MAP.put("png", MediaType.IMAGE_PNG);
        MEDIA_TYPE_MAP.put("zip", MediaType.APPLICATION_OCTET_STREAM);
    }

    @GetMapping("/classified")
    public ResponseEntity<List<String>> listClassifiedFiles() {
        String bucketName = "clasificado";
        List<String> fileUrls = new ArrayList<>();

        try {
            List<String> fileNames = minioService.listFiles(bucketName);

            for (String fileName : fileNames) {
                String url = minioService.getPresignedUrl(bucketName, fileName);
                fileUrls.add(url);
            }

            return ResponseEntity.ok(fileUrls);
        } catch (MinioException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Sube un archivo al bucket de MinIO.
     *
     * @param file Archivo a subir.
     * @return Mensaje indicando que el archivo se ha subido correctamente.
     */
    @PostMapping("/uploadArchivo")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String bucketName = "sin-clasificar"; // Nombre del cubo donde se almacenan las imágenes pendientes de clasificar
        String fileName = file.getOriginalFilename();
        minioService.uploadFile(bucketName, fileName, file);
        return  "Archivo subido con éxito: " + fileName;
    }

    /**
     * Descarga un archivo del bucket de MinIO.
     *
     * @param fileName Nombre del archivo a descargar.
     * @return Respuesta con los bytes del archivo y los encabezados adecuados para la descarga.
     */
    @GetMapping("/downloadFile/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        String bucketName = "clasificado"; // Nombre del cubo de donde se descargan los archivos

        try {
            // Descargar la imagen del bucket
            InputStream fileInputStream = minioService.downloadFile(bucketName, fileName);
            byte[] fileBytes = fileInputStream.readAllBytes();

            // Obtener información asociada al nombre del archivo (por ejemplo, de MongoDB)
            String fileMetadata = imagenService.writeMetadataToFile(fileName);

            // Crear un archivo de texto con la información y los bytes del archivo original
            byte[] textBytes = createTextFile(fileMetadata);

            // Combinar los bytes del archivo de texto y los bytes del archivo original en un solo archivo ZIP
            byte[] zipBytes = createZipFile(fileName, fileBytes, textBytes);

            // Configurar las cabeceras de la respuesta para la descarga del archivo ZIP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName + ".zip");

            // Devolver la respuesta con el archivo ZIP
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error al descargar el archivo: " + e.getMessage()).getBytes());
        }
    }


    /**
     * Mueve las imágenes del bucket "sin-clasificar" al bucket "clasificado".
     * Este método es invocado al recibir una solicitud POST en "/api/moveImages".
     *
     * @return ResponseEntity con un mensaje indicando el resultado de la operación.
     */
    @PostMapping("/moveImages")
    public ResponseEntity<String> clasificar() {
        try {
            scheduledTasks.clasificarImagenesScheduled();
            return ResponseEntity.ok("Imágenes clasificadas con éxito.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al clasificar las imágenes: " + e.getMessage());
        }
    }

    /**
     * Guarda la información de una imagen en MongoDB.
     *
     * @param imagen Información de la imagen a guardar.
     *               @PostMapping("/saveInfo")
     *     public void save(@RequestBody Imagen imagen) {
     *         imagenService.save(imagen);
     *     }
     */


    /**
     * Obtiene todos los registros de imágenes.
     *
     * @return Lista de todas las imágenes almacenadas.
     */
    @GetMapping("/infoImagenes")
    public List<Imagen> getAll() {
        return imagenService.findAll();
    }

    /**
     * Obtiene el tipo de archivo correspondiente a partir del nombre del archivo.
     *
     * @param fileName Nombre del archivo.
     * @return Tipo de medio correspondiente al archivo.
     */
    private MediaType getMediaTypeForFileName(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return MEDIA_TYPE_MAP.getOrDefault(extension, MediaType.APPLICATION_OCTET_STREAM);
    }
}
