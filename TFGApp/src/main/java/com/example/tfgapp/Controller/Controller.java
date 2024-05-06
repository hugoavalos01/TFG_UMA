package com.example.tfgapp.Controller;

import com.example.tfgapp.Entity.Imagen;
import com.example.tfgapp.Service.MinIOService;
import com.example.tfgapp.Service.ImagenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class Controller {

    private final ImagenService imagenService;

    @Autowired
    private MinIOService minioService;

    /**
     * Sube un archivo al bucket de MinIO
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String bucketName = "sin-clasificar"; // Nombre del cubo donde se almacenan las imagenes pendientes de clasificar
        String fileName = file.getOriginalFilename();
        minioService.uploadFile(bucketName, fileName, file);
        imagenService.saveAnotado(fileName);
        return "File uploaded successfully: " + fileName;
    }

    /**
     * Prueba guardar informacion de imagen en mongodb
     *
     * @param imagen
     */
    @PostMapping("/save")
    public void save(@RequestBody Imagen imagen) {
        imagenService.save(imagen);
    }

    /**
     * Prueba obtener todos los registros de imagenes
     *
     * @return
     */
    @GetMapping("/imagenes")
    public List<Imagen> getAll() {
        return imagenService.findAll();
    }

}
