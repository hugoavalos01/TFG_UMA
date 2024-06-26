package com.example.tfgapp;

import io.minio.MinioClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TfgAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TfgAppApplication.class, args);
	}

	@Bean
	public MinioClient minioClient() {
		return MinioClient.builder()
				.endpoint("http://localhost:9000")
				.credentials("tfguma24", "tfguma24")
				.build();
	}

}
