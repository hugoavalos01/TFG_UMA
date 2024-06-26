package com.example.tfgapp.Service;

import com.example.tfgapp.Entity.Imagen;
import com.example.tfgapp.Repository.ImagenRepository;
import com.example.tfgapp.Utils.DirectoryDeleter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.List;
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
            // Listar las imágenes en el bucket "sin-clasificar"
            List<String> fileNames = minioService.listFiles(sourceBucket);

            for (String fileName : fileNames) {
                // Descargar la imagen del bucket "sin-clasificar"
                String filePath = minioService.downloadTempFile(sourceBucket, fileName);

                // Ruta al intérprete de Python del entorno virtual
                String pythonInterpreter = ".\\yolov5\\tfg_env\\Scripts\\python.exe";

                // Ruta al script Python
                String pythonScript = ".\\yolov5\\detect.py";

                // Comando para ejecutar el script Python con el intérprete del entorno virtual
                List<String> pythonCommand = Arrays.asList(pythonInterpreter, pythonScript,
                        "--weights", "yolov5/total_model.pt", "--img", "1024", "--name", "total", "--project", "yolov5/results/",
                        "--save-txt", "--save-conf", "--source", "yolov5/images/", "--max-det", "1", "--conf-thres", "0.5"                );

                // Ejecutar el comando para ejecutar el script Python
                String commandOutput = executeCommand(pythonCommand);

                minioService.deleteFile(fileName);
                minioService.uploadClassifiedFile(fileName);

                // Guardar información de la imagen en el repositorio
                saveClassifiedImagen(fileName);
                borrarDirectorios();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to classify images", e);
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
    public Imagen findByPathMinIO(String fileName) {
        return imagenRepository.findByPathMinIO(fileName);
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
