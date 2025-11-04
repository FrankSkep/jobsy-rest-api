package com.fran.jobsy.app.service.imagestorage;

import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

public interface ImageStorageService {

    ImageUploadResult upload(MultipartFile file);

    List<ImageUploadResult> uploadAll(List<MultipartFile> files);

    void deleteSafely(String publicId);

    void deleteAllSafely(Collection<String> publicIds);
}
