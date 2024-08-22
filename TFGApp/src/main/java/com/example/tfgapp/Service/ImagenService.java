package com.example.tfgapp.Service;

import com.example.tfgapp.Entity.Imagen;
import com.example.tfgapp.Repository.ImagenRepository;
import com.example.tfgapp.Utils.DirectoryDeleter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImagenService {
    private final ImagenRepository imagenRepository;

    private final MinIOService minioService;

    /**
     * Ejecuta el script de clasificacion de imagenes, borra la imagen inicial del bucket no clasificado y sube
     * la imagen clasificada al bucket clasificado
     *
     * @throws IOException
     */
    public void clasificarImagenes() throws IOException {
        String sourceBucket = "sin-clasificar";
        borrarDirectorios();

        try {
            List<String> fileNames = minioService.listFiles(sourceBucket);

            for (String fileName : fileNames) {
                processFile(sourceBucket, fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to classify images", e);
        } finally {
            borrarDirectorios();
        }
    }

    /**
     * Borra el contenido los directorios especificados
     * @throws IOException
     */
    private void borrarDirectorios() throws IOException {
        String rutaImagenes = ".\\yolov5\\results";
        String rutaResultados = ".\\yolov5\\images";

        DirectoryDeleter directoryDeleter = new DirectoryDeleter();
        directoryDeleter.borrarContenidoDirectorio(rutaImagenes);
        directoryDeleter.borrarContenidoDirectorio(rutaResultados);
    }

    private void processFile(String sourceBucket, String fileName) throws Exception {
        minioService.downloadTempFile(sourceBucket, fileName);

        executePythonScript();

        minioService.deleteFile(fileName);
        minioService.uploadClassifiedFile(fileName);

        saveClassifiedImagen(fileName);
    }

    private void executePythonScript() {
        String pythonInterpreter = ".\\yolov5\\tfg_env\\Scripts\\python.exe";
        String pythonScript = ".\\yolov5\\detect.py";

        List<String> pythonCommand = Arrays.asList(
                pythonInterpreter, pythonScript,
                "--weights", "yolov5/total_model.pt",
                "--img", "1024",
                "--name", "total",
                "--project", "yolov5/results/",
                "--save-txt", "--save-conf",
                "--source", "yolov5/images/",
                "--max-det", "1",
                "--conf-thres", "0.5"
        );

        executeCommand(pythonCommand);
    }

    /**
     * Ejecuta el script
     * @param command
     * @return
     */
    private String executeCommand(List<String> command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true); // Redirige la salida de error al flujo de salida
            Process process = processBuilder.start();

            // Lee la salida del proceso
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Espera a que el proceso termine
            int exitCode = process.waitFor();

            // Si el proceso termina con un código de salida distinto de 0, lanza una excepción
            if (exitCode != 0) {
                System.out.println("Command output:");
                System.out.println(output.toString());
                throw new RuntimeException("Command execution failed with exit code: " + exitCode);
            }

            return output.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to execute command", e);
        }
    }
    public void saveClassifiedImagen(String nombre) throws IOException {
        int dotIndex = nombre.lastIndexOf('.');
        String nombreTXT = nombre.substring(0, dotIndex);
        String anotaciones = ".\\yolov5\\results\\total\\labels\\"+nombreTXT+".txt";
        String anotacion = readFirstWord(anotaciones);
        Imagen imagen = new Imagen(nombre, anotacion, "false");
        imagenRepository.save(imagen);
    }

    private static String readFirstWord(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line = br.readLine(); // Leer la primera línea
            if (line != null && !line.isEmpty()) {
                String[] words = line.split("\\s+"); // Dividir la línea en palabras
                if (words.length > 0) {
                    return words[0]; // Devolver la primera palabra
                }
            }
        }
        return null; // Si el archivo está vacío o no contiene palabras
    }
    public List<Imagen> findAll() {
        return imagenRepository.findAll();
    }

    // Método común para obtener archivos clasificados, según criterio de validación
    public List<Map<String, String>> getFilesData(boolean isValidado) throws Exception {
        String bucketName = "clasificado";
        List<Map<String, String>> fileDataList = new ArrayList<>();
        List<String> fileNames;

        if (isValidado) {
            fileNames = findAllValidadas(); // Archivos validados
        } else {
            fileNames = findAllSinValidar(); // Archivos sin validar
        }

        for (String fileName : fileNames) {
            Map<String, String> fileData = new HashMap<>();
            String url = minioService.getPresignedUrl(bucketName, fileName);
            fileData.put("fileName", fileName);
            fileData.put("url", url);
            fileDataList.add(fileData);
        }

        return fileDataList;
    }

    public List<String> findAllSinValidar() {
        List<Imagen> unvalidatedImages = imagenRepository.findAllByValidado("false");
        return unvalidatedImages.stream()
                .map(Imagen::getPathMinIO)
                .collect(Collectors.toList());
    }
    public List<String> findAllValidadas() {
        List<Imagen> imagenesValidadas = imagenRepository.findAllValidated();
        return imagenesValidadas.stream()
                .map(Imagen::getPathMinIO)
                .collect(Collectors.toList());
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
    public void actualizarEstadoValidacion(String fileName, String validado) {
        Imagen imagen = imagenRepository.findByPathMinIO(fileName);
        if (imagen != null) {
            imagen.setValidado(validado);
            imagenRepository.save(imagen);
        } else {
            throw new RuntimeException("Imagen no encontrada");
        }
    }
}
