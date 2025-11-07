package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.common.FileValidator;
import com.fran.jobsy.app.common.UriBuilder;
import com.fran.jobsy.app.dto.offeringphoto.OfferingPhotoResponse;
import com.fran.jobsy.app.service.offeringphoto.OfferingPhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/offerings")
@RequiredArgsConstructor
@Tag(name = "Offering Photos", description = "Operaciones relacionadas con las fotos de los offerings")
public class OfferingPhotoController {

    private final OfferingPhotoService offeringPhotoService;
    private final FileValidator fileValidator;

    @PostMapping("/{offeringId}/photos")
    @Operation(summary = "Agregar una foto a un offering", description = "Permite al propietario del offering agregar una foto.")
    public ResponseEntity<OfferingPhotoResponse> addPhotoToOffering(
            @PathVariable Long offeringId,
            @RequestParam("file") MultipartFile photo) {
        fileValidator.validate(photo);
        OfferingPhotoResponse response = offeringPhotoService.addPhotoToOffering(offeringId, photo);
        URI location = UriBuilder.buildCreatedLocation(response.id());
        return ResponseEntity.created(location).body(response);
    }

    @PostMapping("/{offeringId}/photos/batch")
    @Operation(summary = "Agregar múltiples fotos a un offering", description = "Permite al propietario del offering agregar múltiples fotos a la vez.")
    public ResponseEntity<List<OfferingPhotoResponse>> addPhotosToOffering(
            @PathVariable Long offeringId,
            @RequestParam("files") List<MultipartFile> photos) {
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

