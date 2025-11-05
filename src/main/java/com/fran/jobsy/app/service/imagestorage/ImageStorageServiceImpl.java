package com.fran.jobsy.app.service.imagestorage;

// java

import com.fran.jobsy.app.exception.custom.CloudinaryException;
import com.fran.jobsy.app.service.cloudinary.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageStorageServiceImpl implements ImageStorageService {

    private final CloudinaryService cloudinaryService;

    @Override
    public ImageUploadResult upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CloudinaryException("Archivo inválido para upload");
        }
        try {
            Map res = cloudinaryService.upload(file);
            String publicId = (String) res.get("public_id");
            String url = (String) res.get("url");
            if (publicId == null || url == null) {
                throw new CloudinaryException("Respuesta de Cloudinary incompleta");
            }
            return new ImageUploadResult(publicId, url);
        } catch (
                IOException e) {
            throw new CloudinaryException("Error al subir imagen: " + e.getMessage());
        }
    }

    @Override
    public List<ImageUploadResult> uploadAll(List<MultipartFile> files) {
        if (files == null || files.isEmpty())
            return List.of();
        return files.stream().map(this::upload).toList();
    }

    @Override
    public void deleteSafely(String publicId) {
        if (publicId == null || publicId.isBlank())
            return;
        try {
            cloudinaryService.delete(publicId);
        } catch (
                IOException e) {
            throw new CloudinaryException("Error al eliminar imagen: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void deleteSafelyAsync(String publicId) {
        if (publicId == null || publicId.isBlank())
            return;
        try {
            cloudinaryService.delete(publicId);
        } catch (
                IOException e) {
            throw new CloudinaryException("Error al eliminar imagen: " + e.getMessage());
        }
    }

    @Override
    public void deleteAllSafely(Collection<String> publicIds) {
        if (publicIds == null || publicIds.isEmpty())
            return;
        for (String id : publicIds) {
            deleteSafely(id);
        }
    }
}

