package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.user.UserWorkPhotoDTO;
import com.fran.jobsy.app.exception.custom.InvalidFileException;
import com.fran.jobsy.app.service.UserWorkPhotoService;
import com.fran.jobsy.app.util.FileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Work Photos", description = "Operaciones sobre el portafolio de fotos de trabajos del usuario")
public class UserWorkPhotoController {

    private final UserWorkPhotoService userWorkPhotoService;
    private final FileValidator fileValidator;

    @PostMapping("/me/portfolio")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Subir fotos de trabajos", description = "Permite a un usuario con rol PROVIDER subir fotos a su portafolio. Requiere autenticación y rol PROVIDER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Fotos subidas correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o archivos no válidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado. Solo PROVIDER")
    })
    public void uploadWorkPhotos(@RequestParam("files") List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new InvalidFileException("Debe enviar al menos un archivo.");
        }
        files.forEach(fileValidator::validate);
        userWorkPhotoService.uploadWorkPhotos(files);
    }

    @GetMapping("/{id}/portfolio")
    @Operation(summary = "Obtener portafolio de usuario", description = "Devuelve la lista de fotos de trabajos de un usuario público.")
    @ApiResponse(responseCode = "200", description = "Lista de fotos obtenida correctamente")
    public List<UserWorkPhotoDTO> getWorkPhotos(@PathVariable Long id) {
        return userWorkPhotoService.getUserWorkPhotos(id);
    }

    @DeleteMapping("me/portfolio/{photoId}")
    @PreAuthorize("hasRole('PROVIDER')")
    @Operation(summary = "Eliminar foto de portafolio", description = "Permite a un usuario con rol PROVIDER eliminar una foto de su portafolio. Requiere autenticación y rol PROVIDER.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Foto eliminada correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado. Solo PROVIDER")
    })
    public void removeWorkPhoto(@PathVariable Long photoId) {
        userWorkPhotoService.removeWorkPhoto(photoId);
    }
}
