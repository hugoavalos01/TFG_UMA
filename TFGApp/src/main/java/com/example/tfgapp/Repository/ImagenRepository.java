package com.example.tfgapp.Repository;

import com.example.tfgapp.Entity.Imagen;
import org.springframework.data.mongodb.repository.MongoRepository;

@org.springframework.stereotype.Repository
public interface ImagenRepository extends MongoRepository<Imagen, String> {
}
