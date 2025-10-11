package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.user.UserPhotoDTO;
import com.fran.jobsy.app.service.UserPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserPhotoController {

    private final UserPhotoService userPhotoService;

    @PostMapping("/me/photo")
    public ResponseEntity<UserPhotoDTO> updateProfileImage(@RequestPart MultipartFile photo) {
        return ResponseEntity.ok(userPhotoService.updateUserPhoto(photo));
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<UserPhotoDTO> getProfileImage(@PathVariable Long id) {
        return ResponseEntity.ok(userPhotoService.getUserPhoto(id));
    }

    @DeleteMapping("/me/photo")
    public ResponseEntity<Void> deleteMyProfileImage() {
        userPhotoService.deleteUserPhoto();
        return ResponseEntity.noContent().build();
    }
}
