package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.user.UserWorkPhotoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserWorkPhotoService {
    void uploadWorkPhotos(List<MultipartFile> files);

    void removeWorkPhoto(Long workPhotoId);

    List<UserWorkPhotoResponse> getUserWorkPhotos(Long userId);
}
