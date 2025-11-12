package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.common.FileValidator;
import com.fran.jobsy.app.dto.offeringphoto.OfferingPhotoResponse;
import com.fran.jobsy.app.exception.custom.InvalidFileException;
import com.fran.jobsy.app.service.offeringphoto.OfferingPhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/offerings")
@RequiredArgsConstructor
@Tag(name = "Offering Photos", description = "Operaciones relacionadas con las fotos de los offerings")
public class OfferingPhotoController {

    private final OfferingPhotoService offeringPhotoService;
    private final FileValidator fileValidator;
    private static final int MAX_PHOTOS = 6;

    @PostMapping("/{offeringId}/photos")
    @Operation(summary = "Agregar fotos a un offering", description = "Permite al propietario agregar una o varias fotos.")
    public ResponseEntity<List<OfferingPhotoResponse>> addPhotosToOffering(
            @PathVariable Long offeringId,
            @RequestParam("files") List<MultipartFile> photos) {

        if (photos == null || photos.isEmpty()) {
            throw new InvalidFileException("Debe enviar al menos un archivo.");
        }

        if (photos.size() > MAX_PHOTOS) {
            throw new InvalidFileException("No puede enviar más de 6 archivos en una sola solicitud.");
        }

        photos.forEach(fileValidator::validate);

        List<OfferingPhotoResponse> responses = offeringPhotoService.addPhotosToOffering(offeringId, photos);

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping("/{offeringId}/photos")
    @Operation(summary = "Obtener fotos de un offering", description = "Devuelve todas las fotos asociadas a un offering.")
    public ResponseEntity<List<OfferingPhotoResponse>> getOfferingPhotos(@PathVariable Long offeringId) {
        List<OfferingPhotoResponse> photos = offeringPhotoService.getOfferingPhotos(offeringId);
        return ResponseEntity.ok(photos);
    }

    @DeleteMapping("/{offeringId}/photos/{photoId}")
    @Operation(summary = "Eliminar una foto de un offering", description = "Permite al propietario del offering eliminar una foto.")
    public ResponseEntity<Void> deleteOfferingPhoto(
            @PathVariable Long offeringId,
            @PathVariable Long photoId) {
        offeringPhotoService.deleteOfferingPhoto(offeringId, photoId);
        return ResponseEntity.noContent().build();
    }
}

