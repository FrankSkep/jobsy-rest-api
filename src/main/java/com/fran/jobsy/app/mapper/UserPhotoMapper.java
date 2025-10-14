package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.user.UserPhotoDTO;
import com.fran.jobsy.app.entity.UserPhoto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPhotoMapper {

    UserPhotoDTO toDTO(UserPhoto userPhoto);
}
