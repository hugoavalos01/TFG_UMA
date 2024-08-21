package com.example.tfgapp;

import com.example.tfgapp.Controller.Controller;
import com.example.tfgapp.Entity.Imagen;
import com.example.tfgapp.Service.ImagenService;
import com.example.tfgapp.Service.MinIOService;
import com.example.tfgapp.Utils.ScheduledTasks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ControllerTest {

    @Mock
    private ImagenService imagenService;

    @Mock
    private MinIOService minioService;

    @Mock
    private ScheduledTasks scheduledTasks;

    @InjectMocks
    private Controller controller;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listClassifiedFiles_ShouldReturnFiles() throws Exception {
        List<String> fileNames = List.of("file1.jpg", "file2.png");
        when(imagenService.findAllValidadas()).thenReturn(fileNames);
        when(minioService.getPresignedUrl(anyString(), anyString())).thenReturn("http://ejemplo.com");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/classified"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fileName").value("file1.jpg"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].url").value("http://ejemplo.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].fileName").value("file2.png"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].url").value("http://ejemplo.com"));
    }

    @Test
    void listSinValidarFiles_ShouldReturnUnvalidatedFiles() throws Exception {
        List<String> fileNames = List.of("file1.jpg", "file2.png");
        when(imagenService.findAllSinValidar()).thenReturn(fileNames);
        when(minioService.getPresignedUrl(anyString(), anyString())).thenReturn("http://ejemplo.com");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/noValidado"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fileName").value("file1.jpg"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].url").value("http://ejemplo.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].fileName").value("file2.png"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].url").value("http://ejemplo.com"));
    }

    @Test
    void validarImagen_ShouldReturnSuccessMessage() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("fileName", "file1.jpg");
        request.put("validado", "true");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/validarImagen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fileName\": \"file1.jpg\", \"validado\": \"true\"}"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Imagen validada correctamente"));
    }

    @Test
    void uploadFile_ShouldReturnSuccessMessage() throws Exception {
        MultipartFile file = new MockMultipartFile("file", "file1.jpg", MediaType.IMAGE_JPEG_VALUE, new byte[0]);
        when(minioService.uploadFile(anyString(), any(MultipartFile.class))).thenReturn("El archivo se ha subido con éxito:file1.jpg");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/uploadArchivo")
                        .file((MockMultipartFile) file))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("El archivo se ha subido con éxito:file1.jpg"));
    }

    @Test
    void clasificar_ShouldReturnSuccessMessage() throws Exception {
        when(minioService.listFiles(anyString())).thenReturn(List.of("file1.jpg", "file2.png"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/moveImages"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Imágenes clasificadas con éxito."));
    }

    @Test
    void getStatus_ShouldReturnStatus() throws Exception {
        when(scheduledTasks.isClasificacionCompletada()).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/moveImages/status"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Completado"));
    }

    @Test
    void getAll_ShouldReturnAllImages() throws Exception {
        List<Imagen> imagenes = List.of(new Imagen("file1.jpg", "annotation", "true"));
        when(imagenService.findAll()).thenReturn(imagenes);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/infoImagenes"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].pathMinIO").value("file1.jpg"));
    }
}

