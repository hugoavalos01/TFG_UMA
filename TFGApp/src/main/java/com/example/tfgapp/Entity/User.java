package com.example.tfgapp.Entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

@Data
@Document(value = "users")
public class User {

    @Id
    private String id;

    private String username;
    private String password;


}
