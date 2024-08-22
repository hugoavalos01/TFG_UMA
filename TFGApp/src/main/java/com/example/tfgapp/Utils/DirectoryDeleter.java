package com.example.tfgapp.Utils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class DirectoryDeleter {

    public static void borrarContenidoDirectorio(String ruta) throws IOException {
        Path path = Paths.get(ruta);

        if (!Files.exists(path) || !Files.isDirectory(path)) {
            System.out.println("El directorio no existe o no es un directorio: " + ruta);
            return;
        }

        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (!dir.equals(path)) { // No borrar el directorio raíz
                    Files.delete(dir);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        System.out.println("Contenido del directorio borrado con éxito: " + ruta);
    }

}
