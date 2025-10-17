package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.user.UserWorkPhotoDTO;
import com.fran.jobsy.app.entity.UserWorkPhoto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserWorkPhotoMapper {
    UserWorkPhotoDTO toDto(UserWorkPhoto entity);

    List<UserWorkPhotoDTO> toDtoList(List<UserWorkPhoto> entities);

    UserWorkPhoto toEntity(UserWorkPhotoDTO dto);
}
