package com.example.tfgapp.Entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "Anotaciones")
public class Imagen {

    public Imagen(String pathMinIO, String anotaciones, String validado) {
        this.pathMinIO = pathMinIO;
        this.anotaciones = anotaciones;
        this.validado  =  validado;
    }

    @Id
    private String id;
    @Indexed(unique = true)
    private String pathMinIO;
    private String anotaciones;
    private String validado;

}
