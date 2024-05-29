package com.example.tfgapp.Service;

import com.example.tfgapp.Entity.Imagen;
import com.example.tfgapp.Repository.ImagenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImagenService {
    private final ImagenRepository imagenRepository;

    public void save(Imagen imagen) {
        imagenRepository.save(imagen);
    }

    public List<Imagen> findAll() {
        return imagenRepository.findAll();
    }

    public void saveAnotado(String fileName) {
        Imagen imagen = new Imagen(fileName,"anotado");
        imagenRepository.save(imagen);
    }

    public String writeMetadataToFile(String fileName) {
        // Consultar MongoDB para obtener la información asociada al nombre del archivo
        Imagen imagen = imagenRepository.findByPathMinIO(fileName);

        // Verificar si se encontró la imagen
        if (imagen != null) {
            // Construir el contenido del archivo de texto con la información obtenida de la imagen
            String fileMetadata = "pathMinIO: \"" + imagen.getPathMinIO() + "\"\n" +
                    "anotaciones: \"" + imagen.getAnotaciones() + "\"";
            return fileMetadata;
        } else {
            // Manejar el caso en el que no se encuentra la información asociada al nombre del archivo
            throw new RuntimeException("No se encontró información para el archivo: " + fileName);
        }
    }


    public Imagen findByPathMinIO(String fileName) {
        return imagenRepository.findByPathMinIO(fileName);
    }
}
