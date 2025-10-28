package com.fran.jobsy.app.service.userphoto;

import com.fran.jobsy.app.dto.user.UserPhotoResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserPhotoService {
    UserPhotoResponse updateUserPhoto(MultipartFile file);

    UserPhotoResponse getUserPhoto(Long id);

    void deleteUserPhoto();
}
