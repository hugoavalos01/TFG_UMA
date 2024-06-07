/*
package com.example.tfgapp.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Construir el objeto de respuesta JSON
        String username = authentication.getName();

        ResponseData responseData = new ResponseData("Inicio de sesión exitoso", 200, username);

        // Escribir el objeto de respuesta JSON en el cuerpo de la respuesta
        PrintWriter writer = response.getWriter();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        writer.println(objectMapper.writeValueAsString(responseData));
    }


    // Clase para representar la respuesta JSON
    private static class ResponseData {
        private String message;
        private int status;
        private String username; // Agregar campo para el nombre de usuario

        public ResponseData(String message, int status, String username) {
            this.message = message;
            this.status = status;
            this.username = username;
        }

        public String getMessage() {
            return message;
        }

        public int getStatus() {
            return status;
        }

        public String getUsername() {
            return username;
        }
    }

}*/
