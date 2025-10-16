package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.user.UserPhotoDTO;
import com.fran.jobsy.app.service.UserPhotoService;
import com.fran.jobsy.app.util.FileValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Photos", description = "Operaciones relacionadas con la foto de perfil del usuario")
public class UserPhotoController {

    private final UserPhotoService userPhotoService;
    private final FileValidator fileValidator;

    @PostMapping("/me/photo")
    @Operation(summary = "Actualizar foto de perfil", description = "Permite al usuario autenticado actualizar su foto de perfil.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Foto de perfil actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Archivo no válido")
    })
    public ResponseEntity<UserPhotoDTO> updateProfileImage(@RequestParam("file") MultipartFile photo) {
        fileValidator.validate(photo);
        return ResponseEntity.ok(userPhotoService.updateUserPhoto(photo));
    }

    @GetMapping("/{id}/photo")
    @Operation(summary = "Obtener foto de perfil", description = "Devuelve la foto de perfil pública de un usuario.")
    @ApiResponse(responseCode = "200", description = "Foto de perfil obtenida correctamente")
    public ResponseEntity<UserPhotoDTO> getProfileImage(@PathVariable Long id) {
        return ResponseEntity.ok(userPhotoService.getUserPhoto(id));
    }

    @DeleteMapping("/me/photo")
    @Operation(summary = "Eliminar mi foto de perfil", description = "Permite al usuario autenticado eliminar su foto de perfil.")
    @ApiResponse(responseCode = "204", description = "Foto de perfil eliminada correctamente")
    public ResponseEntity<Void> deleteMyProfileImage() {
        userPhotoService.deleteUserPhoto();
        return ResponseEntity.noContent().build();
    }
}
