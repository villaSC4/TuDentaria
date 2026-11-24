package com.utp.tudentaria.service;

import com.utp.tudentaria.exception.NegocioException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UploadFileService {

    private static final Path CARPETA = Paths.get("uploads").toAbsolutePath().normalize();

    /** Guarda una imagen validada con nombre generado por el servidor (nunca el original). */
    public String guardarImagen(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        byte[] bytes = file.getBytes();
        String extension = detectarExtension(bytes);
        if (extension == null) {
            throw new NegocioException("La foto debe ser una imagen JPG, PNG o WEBP válida.");
        }

        Files.createDirectories(CARPETA);
        String nombre = UUID.randomUUID() + "." + extension;
        Path destino = CARPETA.resolve(nombre).normalize();
        if (!destino.startsWith(CARPETA)) {
            throw new NegocioException("Nombre de archivo no permitido.");
        }
        Files.write(destino, bytes);
        return nombre;
    }

    public void eliminarImagen(String nombreImagen) {
        if (nombreImagen == null || nombreImagen.isBlank()) {
            return;
        }
        // getFileName() descarta cualquier ruta ("../") que venga en el valor.
        Path destino = CARPETA.resolve(Paths.get(nombreImagen).getFileName().toString()).normalize();
        if (destino.startsWith(CARPETA)) {
            try {
                Files.deleteIfExists(destino);
            } catch (IOException ignorado) {
                // Si no se puede borrar el archivo no se debe bloquear la operación principal.
            }
        }
    }

    /** Se mira el contenido real del archivo (firma), no el nombre ni el Content-Type que manda el cliente. */
    private String detectarExtension(byte[] b) {
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "jpg";
        }
        if (b.length >= 8 && (b[0] & 0xFF) == 0x89 && b[1] == 'P' && b[2] == 'N' && b[3] == 'G') {
            return "png";
        }
        if (b.length >= 12 && new String(b, 0, 4, StandardCharsets.US_ASCII).equals("RIFF")
                && new String(b, 8, 4, StandardCharsets.US_ASCII).equals("WEBP")) {
            return "webp";
        }
        return null;
    }
}