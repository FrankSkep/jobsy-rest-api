package com.fran.jobsy.app.common;

import com.fran.jobsy.app.exception.custom.InvalidFileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component
public class FileValidator {

    private static final long MAX_SIZE = 10L * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/jpg",
            "image/webp"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            ".jpg",
            ".jpeg",
            ".png",
            ".webp"
    );

    public void validate(MultipartFile file) {
        validateNotEmpty(file);
        validateSize(file);
        validateFilename(file);
        validateMimeType(file);
    }

    private void validateNotEmpty(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("Archivo vacío o ausente");
        }
    }

    private void validateSize(MultipartFile file) {
        if (file.getSize() > MAX_SIZE) {
            throw new InvalidFileException("El archivo excede el límite de 10MB");
        }
    }

    private void validateFilename(MultipartFile file) {
        String filename = file.getOriginalFilename();

        if (filename == null || filename.isBlank()) {
            throw new InvalidFileException("Nombre de archivo inválido");
        }

        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            throw new InvalidFileException("Nombre de archivo inseguro");
        }

        String extension = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidFileException("Extensión de archivo no permitida: " + extension);
        }
    }

    private void validateMimeType(MultipartFile file) {
        String detectedType = detectMimeType(file);
        if (!ALLOWED_TYPES.contains(detectedType)) {
            throw new InvalidFileException("Tipo de archivo no permitido: " + detectedType);
        }
    }

    private String detectMimeType(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] header = is.readNBytes(16);

            if (isJPEG(header))
                return "image/jpeg";
            if (isPNG(header))
                return "image/png";
            if (isWEBP(header))
                return "image/webp";

            String contentType = file.getContentType();
            return contentType != null ? contentType : "application/octet-stream";
        } catch (
                IOException e) {
            throw new InvalidFileException("No se pudo leer el archivo: " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot) : "";
    }

    private static boolean isJPEG(byte[] header) {
        return header.length >= 3
                && (header[0] & 0xFF) == 0xFF
                && (header[1] & 0xFF) == 0xD8
                && (header[2] & 0xFF) == 0xFF;
    }

    private static boolean isPNG(byte[] header) {
        return header.length >= 8
                && (header[0] & 0xFF) == 0x89
                && header[1] == 'P'
                && header[2] == 'N'
                && header[3] == 'G'
                && (header[4] & 0xFF) == 0x0D
                && (header[5] & 0xFF) == 0x0A
                && (header[6] & 0xFF) == 0x1A
                && (header[7] & 0xFF) == 0x0A;
    }

    private static boolean isWEBP(byte[] header) {
        return header.length >= 12
                && header[0] == 'R'
                && header[1] == 'I'
                && header[2] == 'F'
                && header[3] == 'F'
                && header[8] == 'W'
                && header[9] == 'E'
                && header[10] == 'B'
                && header[11] == 'P';
    }
}
