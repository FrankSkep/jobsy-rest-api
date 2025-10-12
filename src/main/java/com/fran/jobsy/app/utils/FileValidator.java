package com.fran.jobsy.app.utils;

import com.fran.jobsy.app.exception.custom.InvalidFileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
public class FileValidator {

    private static final long MAX_SIZE = 10L * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png");

    public void validate(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new InvalidFileException("Archivo vacío o ausente");

        if (file.getSize() > MAX_SIZE)
            throw new InvalidFileException("El archivo excede el límite de 5MB");

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank())
            throw new InvalidFileException("Nombre de archivo inválido");
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\"))
            throw new InvalidFileException("Nombre de archivo inseguro");

        String detectedType = detectMimeType(file);
        if (!ALLOWED_TYPES.contains(detectedType))
            throw new InvalidFileException("Tipo de archivo no permitido: " + detectedType);
    }

    private String detectMimeType(MultipartFile file) {
        try (var is = file.getInputStream()) {
            is.mark(16);
            byte[] header = is.readNBytes(16);
            if (isJPEG(header))
                return "image/jpeg";
            if (isPNG(header))
                return "image/png";
            String ct = file.getContentType();
            return ct != null ? ct : "application/octet-stream";
        } catch (
                IOException e) {
            throw new IllegalArgumentException("No se pudo leer el archivo", e);
        }
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
}
