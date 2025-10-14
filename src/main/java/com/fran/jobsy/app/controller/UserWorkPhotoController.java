package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.user.UserWorkPhotoDTO;
import com.fran.jobsy.app.exception.custom.InvalidFileException;
import com.fran.jobsy.app.service.UserWorkPhotoService;
import com.fran.jobsy.app.util.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserWorkPhotoController {

    private final UserWorkPhotoService userWorkPhotoService;
    private final FileValidator fileValidator;

    @PostMapping("/me/portfolio")
    public void uploadWorkPhotos(@RequestParam("files") List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new InvalidFileException("Debe enviar al menos un archivo.");
        }
        files.forEach(fileValidator::validate);

        userWorkPhotoService.uploadWorkPhotos(files);
    }

    @GetMapping("/{id}/portfolio")
    public List<UserWorkPhotoDTO> getWorkPhotos(@PathVariable Long id) {
        return userWorkPhotoService.getUserWorkPhotos(id);
    }

    @DeleteMapping("me/portfolio/{photoId}")
    public void removeWorkPhoto(@PathVariable Long photoId) {
        userWorkPhotoService.removeWorkPhoto(photoId);
    }
}
