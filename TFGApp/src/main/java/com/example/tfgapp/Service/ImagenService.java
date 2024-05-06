package com.example.tfgapp.Service;

import com.example.tfgapp.Entity.Imagen;
import com.example.tfgapp.Repository.ImagenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
