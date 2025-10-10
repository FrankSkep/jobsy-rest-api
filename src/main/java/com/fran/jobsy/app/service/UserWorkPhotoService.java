package com.fran.jobsy.app.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserWorkPhotoService {
    void uploadWorkPhotos(List<MultipartFile> files);
    void removeWorkPhoto(Long workPhotoId);
}
