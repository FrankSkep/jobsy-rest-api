package com.fran.jobsy.app.service;

import com.fran.jobsy.app.entity.UserPhoto;
import org.springframework.web.multipart.MultipartFile;

public interface UserPhotoService {
    UserPhoto updateUserPhoto(MultipartFile file);

    void deleteUserPhoto();
}
