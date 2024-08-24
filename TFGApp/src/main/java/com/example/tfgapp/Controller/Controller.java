package com.example.tfgapp.Controller;

import com.example.tfgapp.Entity.Imagen;
import com.example.tfgapp.Service.FileService;
import com.example.tfgapp.Service.MinIOService;
import com.example.tfgapp.Service.ImagenService;
import com.example.tfgapp.Utils.ScheduledTasks;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
    private final FileService fileService;

    /**
     *
     * @return
     */
    @GetMapping("/classified")
    public ResponseEntity<List<Map<String, String>>> listClassifiedFiles() {
        try {
            List<Map<String, String>> fileDataList = imagenService.getFilesData(true);
            return ResponseEntity.ok(fileDataList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Obtiene todas las imagenes que han sido clasificadas pero no han sido validadas aún
     * @return
     */
    @GetMapping("/noValidado")
    public ResponseEntity<List<Map<String, String>>> listSinValidarFiles() {
        try {
            List<Map<String, String>> fileDataList = imagenService.getFilesData(false);
            return ResponseEntity.ok(fileDataList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Guarda en BBDD la validación de una imagen
     * @param request
     * @return
     */
    @PostMapping("/validarImagen")
    public ResponseEntity<String> validarImagen(@RequestBody Map<String, String> request) {
        String fileName = request.get("fileName");
        String validado = request.get("validado");

        try {
            imagenService.actualizarEstadoValidacion(fileName, validado);
            return ResponseEntity.ok("Imagen validada correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error al validar la imagen");
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
        String fileName = file.getOriginalFilename();
        String resultado = minioService.uploadFile(fileName, file);
        return resultado;
    }


    /**
     * Descarga un archivo del bucket de MinIO.
     *
     * @param fileName Nombre del archivo a descargar.
     * @return Respuesta con los bytes del archivo y los encabezados adecuados para la descarga.
     */
    @GetMapping("/downloadFile/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        try {
            // Genera el archivo ZIP con la imagen y la informacion
            byte[] zipBytes = fileService.generateZipWithImageAndInfo(fileName);

            // Configurar las cabeceras de la respuesta para la descarga del archivo ZIP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName + ".zip");

            // Respuesta con el archivo ZIP
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
    public ResponseEntity<String> clasificar() throws Exception {
        if(minioService.listFiles("sin-clasificar").isEmpty()) {
            return ResponseEntity.ok("No hay imagenes por clasificar.");
        }
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
     * Comprueba si ya se han clasificado las imagenes
     *
     * @return
     */

    @GetMapping("/moveImages/status")
    public ResponseEntity<String> getStatus() {
        boolean isComplete = scheduledTasks.isClasificacionCompletada();
        return ResponseEntity.ok(isComplete ? "Completado" : "En progreso");
    }

    /**
     * Obtiene todos los registros de imágenes.
     *
     * @return Lista de todas las imágenes almacenadas.
     */
    @GetMapping("/infoImagenes")
    public List<Imagen> getAll() {
        return imagenService.findAll();
    }


}
