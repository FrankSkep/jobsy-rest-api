package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.user.UserPhotoDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserPhotoService {
    UserPhotoDTO updateUserPhoto(MultipartFile file);

    UserPhotoDTO getUserPhoto(Long id);

    void deleteUserPhoto();
}
