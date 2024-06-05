package com.example.tfgapp.Entity;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Data
@Document(value = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true) // Asegura que el campo 'username' sea único
    private String username;
    private String password;


}
