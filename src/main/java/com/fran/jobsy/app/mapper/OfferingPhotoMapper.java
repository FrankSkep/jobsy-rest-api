package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.offeringphoto.OfferingPhotoResponse;
import com.fran.jobsy.app.entity.OfferingPhoto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OfferingPhotoMapper {

    OfferingPhotoResponse toDTO(OfferingPhoto offeringPhoto);

    List<OfferingPhotoResponse> toDTOList(List<OfferingPhoto> offeringPhotos);
}

