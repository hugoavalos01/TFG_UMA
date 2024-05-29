package com.example.tfgapp.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {

    // Método para crear un archivo de texto con la información proporcionada
    public static byte[] createTextFile(String fileMetadata) {
        String textContent = "Información del archivo:\n" + fileMetadata;
        return textContent.getBytes();
    }

    // Método para combinar los bytes del archivo de texto y los bytes del archivo original en un archivo ZIP
    public static byte[] createZipFile(String fileName, byte[] fileBytes, byte[] textBytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(baos)) {
            // Agregar el archivo original al archivo ZIP
            ZipEntry fileEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(fileEntry);
            zipOut.write(fileBytes, 0, fileBytes.length);
            zipOut.closeEntry();

            // Agregar el archivo de texto al archivo ZIP
            ZipEntry textEntry = new ZipEntry(fileName + "_info.txt");
            zipOut.putNextEntry(textEntry);
            zipOut.write(textBytes, 0, textBytes.length);
            zipOut.closeEntry();
        }
        return baos.toByteArray();
    }
}
