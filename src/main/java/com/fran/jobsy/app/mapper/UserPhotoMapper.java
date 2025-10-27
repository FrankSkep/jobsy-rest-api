package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.user.UserPhotoResponse;
import com.fran.jobsy.app.entity.UserPhoto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPhotoMapper {

    UserPhotoResponse toDTO(UserPhoto userPhoto);
}
