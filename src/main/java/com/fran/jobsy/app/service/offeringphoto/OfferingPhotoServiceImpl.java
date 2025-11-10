package com.fran.jobsy.app.service.offeringphoto;

import com.fran.jobsy.app.common.AuthenticatedUserProvider;
import com.fran.jobsy.app.dto.offeringphoto.OfferingPhotoResponse;
import com.fran.jobsy.app.entity.Offering;
import com.fran.jobsy.app.entity.OfferingPhoto;
import com.fran.jobsy.app.exception.custom.FileOperationException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.exception.custom.UnauthorizedAccessException;
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

        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el offering con id: " + offeringId));

        if (offeringPhotoRepository.countByOfferingId(offeringId) >= 6) {
            throw new FileOperationException("No se pueden agregar más de 6 fotos a un servicio");
        }

        if (!offering.getOwner().getId().equals(userId)) {
            throw new UnauthorizedAccessException("No tienes permiso para agregar fotos a este offering");
        }

        String newImageId = null;

        try {
            ImageUploadResult uploadResult = imageStorageService.upload(file);
            String imageUrl = uploadResult.url();
            String publicId = uploadResult.publicId();
            newImageId = publicId;

            OfferingPhoto offeringPhoto = OfferingPhoto.builder()
                    .imageId(publicId)
                    .url(imageUrl)
                    .offering(offering)
                    .build();

            OfferingPhoto saved = offeringPhotoRepository.save(offeringPhoto);

            return offeringPhotoMapper.toDTO(saved);

        } catch (
                Exception e) {
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

        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el offering con id: " + offeringId));

        if (!offering.getOwner().getId().equals(userId)) {
            throw new UnauthorizedAccessException("No tienes permiso para agregar fotos a este offering");
        }

        List<String> uploadedImageIds = new ArrayList<>();
        List<OfferingPhoto> photosToSave = new ArrayList<>();

        try {
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

            List<OfferingPhoto> saved = offeringPhotoRepository.saveAll(photosToSave);

            return offeringPhotoMapper.toDTOList(saved);

        } catch (
                Exception e) {
            if (!uploadedImageIds.isEmpty()) {
                imageStorageService.deleteAllSafely(uploadedImageIds);
            }
            throw new FileOperationException("Error al agregar las fotos al offering: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OfferingPhotoResponse> getOfferingPhotos(Long offeringId) {
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

        Offering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el offering con id: " + offeringId));

        if (!offering.getOwner().getId().equals(userId)) {
            throw new UnauthorizedAccessException("No tienes permiso para eliminar fotos de este offering");
        }

        OfferingPhoto photo = offeringPhotoRepository.findByIdAndOfferingId(photoId, offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la foto con id: " + photoId + " para el offering con id: " + offeringId));

        String imageId = photo.getImageId();

        offeringPhotoRepository.deleteByIdAndOfferingId(photoId, offeringId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                imageStorageService.deleteSafelyAsync(imageId);
            }
        });
    }
}

