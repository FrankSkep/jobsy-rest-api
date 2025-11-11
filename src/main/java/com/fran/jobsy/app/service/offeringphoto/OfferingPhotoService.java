package com.fran.jobsy.app.service.offeringphoto;

import com.fran.jobsy.app.dto.offeringphoto.OfferingPhotoResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface OfferingPhotoService {

    List<OfferingPhotoResponse> addPhotosToOffering(Long offeringId, List<MultipartFile> files);

    List<OfferingPhotoResponse> getOfferingPhotos(Long offeringId);

    void deleteOfferingPhoto(Long offeringId, Long photoId);
}

