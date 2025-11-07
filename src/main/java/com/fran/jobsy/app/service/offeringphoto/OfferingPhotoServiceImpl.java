package com.fran.jobsy.app.service.offeringphoto;

import com.fran.jobsy.app.common.AuthenticatedUserProvider;
import com.fran.jobsy.app.dto.offeringphoto.OfferingPhotoResponse;
import com.fran.jobsy.app.entity.Offering;
import com.fran.jobsy.app.entity.OfferingPhoto;
import com.fran.jobsy.app.exception.custom.FileOperationException;
import com.fran.jobsy.app.exception.custom.UnauthorizedAccessException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.mapper.OfferingPhotoMapper;
import com.fran.jobsy.app.repository.OfferingPhotoRepository;
import com.fran.jobsy.app.repository.OfferingRepository;
import com.fran.jobsy.app.service.imagestorage.ImageStorageService;
import com.fran.jobsy.app.service.imagestorage.ImageUploadResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OfferingPhotoServiceImpl implements OfferingPhotoService {

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final ImageStorageService imageStorageService;
    private final OfferingPhotoRepository offeringPhotoRepository;
    private final OfferingRepository offeringRepository;
    private final OfferingPhotoMapper offeringPhotoMapper;

    @Override
    @Transactional
    public OfferingPhotoResponse addPhotoToOffering(Long offeringId, MultipartFile file) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        // Verificar que el offering existe y pertenece al usuario autenticado
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el offering con id: " + offeringId));

        if (!offering.getOwner().getId().equals(userId)) {
            throw new UnauthorizedAccessException("No tienes permiso para agregar fotos a este offering");
        }

        String newImageId = null;

        try {
            // Subir imagen
            ImageUploadResult uploadResult = imageStorageService.upload(file);
            String imageUrl = uploadResult.url();
            String publicId = uploadResult.publicId();
            newImageId = publicId;

            // Crear y persistir la foto
            OfferingPhoto offeringPhoto = OfferingPhoto.builder()
                    .imageId(publicId)
                    .url(imageUrl)
                    .offering(offering)
                    .build();

            OfferingPhoto saved = offeringPhotoRepository.save(offeringPhoto);

            return offeringPhotoMapper.toDTO(saved);

        } catch (Exception e) {
            // En caso de fallo, limpiar la imagen subida
            if (newImageId != null) {
                imageStorageService.deleteSafely(newImageId);
            }
            throw new FileOperationException("Error al agregar la foto al offering: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public List<OfferingPhotoResponse> addPhotosToOffering(Long offeringId, List<MultipartFile> files) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        // Verificar que el offering existe y pertenece al usuario autenticado
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el offering con id: " + offeringId));

        if (!offering.getOwner().getId().equals(userId)) {
            throw new UnauthorizedAccessException("No tienes permiso para agregar fotos a este offering");
        }

        List<String> uploadedImageIds = new ArrayList<>();
        List<OfferingPhoto> photosToSave = new ArrayList<>();

        try {
            // Subir todas las imágenes
            List<ImageUploadResult> uploadResults = imageStorageService.uploadAll(files);

            for (ImageUploadResult result : uploadResults) {
                uploadedImageIds.add(result.publicId());

                OfferingPhoto offeringPhoto = OfferingPhoto.builder()
                        .imageId(result.publicId())
                        .url(result.url())
                        .offering(offering)
                        .build();

                photosToSave.add(offeringPhoto);
            }

            // Guardar todas las fotos
            List<OfferingPhoto> saved = offeringPhotoRepository.saveAll(photosToSave);

            return offeringPhotoMapper.toDTOList(saved);

        } catch (Exception e) {
            // En caso de fallo, limpiar todas las imágenes subidas
            if (!uploadedImageIds.isEmpty()) {
                imageStorageService.deleteAllSafely(uploadedImageIds);
            }
            throw new FileOperationException("Error al agregar las fotos al offering: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferingPhotoResponse> getOfferingPhotos(Long offeringId) {
        // Verificar que el offering existe
        if (!offeringRepository.existsById(offeringId)) {
            throw new ResourceNotFoundException("No se encontró el offering con id: " + offeringId);
        }

        List<OfferingPhoto> photos = offeringPhotoRepository.findByOfferingId(offeringId);
        return offeringPhotoMapper.toDTOList(photos);
    }

    @Override
    @Transactional
    public void deleteOfferingPhoto(Long offeringId, Long photoId) {
        Long userId = authenticatedUserProvider.getAuthenticatedUserId();

        // Verificar que el offering existe y pertenece al usuario autenticado
        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el offering con id: " + offeringId));

        if (!offering.getOwner().getId().equals(userId)) {
            throw new UnauthorizedAccessException("No tienes permiso para eliminar fotos de este offering");
        }

        // Verificar que la foto existe y pertenece al offering
        OfferingPhoto photo = offeringPhotoRepository.findByIdAndOfferingId(photoId, offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la foto con id: " + photoId + " para el offering con id: " + offeringId));

        String imageId = photo.getImageId();

        // Eliminar la foto de la base de datos
        offeringPhotoRepository.deleteByIdAndOfferingId(photoId, offeringId);

        // Después de confirmar la transacción, eliminar la imagen del almacenamiento
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                imageStorageService.deleteSafelyAsync(imageId);
            }
        });
    }
}

