package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.user.UserWorkPhotoResponse;
import com.fran.jobsy.app.entity.UserWorkPhoto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserWorkPhotoMapper {
    UserWorkPhotoResponse toDto(UserWorkPhoto entity);

    List<UserWorkPhotoResponse> toListDTO(List<UserWorkPhoto> entities);
}
