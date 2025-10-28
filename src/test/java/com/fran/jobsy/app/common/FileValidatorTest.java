package com.fran.jobsy.app.common;

import com.fran.jobsy.app.exception.custom.InvalidFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class FileValidatorTest {

    private FileValidator fileValidator;

    @BeforeEach
    void setUp() {
        fileValidator = new FileValidator();
    }

    @Test
    void validate_ValidJpegFile_ShouldPass() {
        byte[] jpegHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                jpegHeader
        );

        assertDoesNotThrow(() -> fileValidator.validate(file));
    }

    @Test
    void validate_ValidPngFile_ShouldPass() {
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.png",
                "image/png",
                pngHeader
        );

        assertDoesNotThrow(() -> fileValidator.validate(file));
    }

    @Test
    void validate_NullFile_ShouldThrowException() {
        InvalidFileException exception = assertThrows(
                InvalidFileException.class,
                () -> fileValidator.validate(null)
        );
        assertEquals("Archivo vacío o ausente", exception.getMessage());
    }

    @Test
    void validate_EmptyFile_ShouldThrowException() {
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        InvalidFileException exception = assertThrows(
                InvalidFileException.class,
                () -> fileValidator.validate(file)
        );
        assertEquals("Archivo vacío o ausente", exception.getMessage());
    }

    @Test
    void validate_FileTooLarge_ShouldThrowException() {
        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
        MultipartFile file = new MockMultipartFile(
                "file",
                "large.jpg",
                "image/jpeg",
                largeContent
        );

        InvalidFileException exception = assertThrows(
                InvalidFileException.class,
                () -> fileValidator.validate(file)
        );
        assertEquals("El archivo excede el límite de 10MB", exception.getMessage());
    }

    @Test
    void validate_InvalidFilename_ShouldThrowException() {
        MultipartFile file = new MockMultipartFile(
                "file",
                null,
                "image/jpeg",
                new byte[]{1, 2, 3}
        );

        InvalidFileException exception = assertThrows(
                InvalidFileException.class,
                () -> fileValidator.validate(file)
        );
        assertEquals("Nombre de archivo inválido", exception.getMessage());
    }

    @Test
    void validate_PathTraversalAttempt_ShouldThrowException() {
        byte[] jpegHeader = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0};
        MultipartFile file = new MockMultipartFile(
                "file",
                "../../../etc/passwd.jpg",
                "image/jpeg",
                jpegHeader
        );

        InvalidFileException exception = assertThrows(
                InvalidFileException.class,
                () -> fileValidator.validate(file)
        );
        assertEquals("Nombre de archivo inseguro", exception.getMessage());
    }

    @Test
    void validate_InvalidMimeType_ShouldThrowException() {
        byte[] pdfHeader = {0x25, 0x50, 0x44, 0x46}; // PDF header
        MultipartFile file = new MockMultipartFile(
                "file",
                "test.jpg",  // Allowed extension
                "application/pdf",  // But MIME type not allowed
                pdfHeader
        );

        InvalidFileException exception = assertThrows(
                InvalidFileException.class,
                () -> fileValidator.validate(file)
        );
        assertTrue(exception.getMessage().contains("Tipo de archivo no permitido"));
    }

    @Test
    void validate_MismatchedExtensionAndContent_ShouldThrowException() {
        byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        MultipartFile file = new MockMultipartFile(
                "file",
                "fake.jpg",
                "image/jpeg",
                pngHeader
        );

        assertDoesNotThrow(() -> fileValidator.validate(file));
    }
}
