package com.example.tfgapp.Repository;

import com.example.tfgapp.Entity.Imagen;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ImagenRepository extends MongoRepository<Imagen, String> {
    Imagen findByPathMinIO(String pathMinIO);

    List<Imagen> findAllByValidado(String validado);

    @Query("{ 'validado' : { $ne: 'false' } }")
    List<Imagen> findAllValidated();
}
