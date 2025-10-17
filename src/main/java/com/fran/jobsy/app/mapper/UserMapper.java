package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.user.*;
import com.fran.jobsy.app.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserPhotoMapper.class, UserWorkPhotoMapper.class})
public interface UserMapper {

    UserBasicDTO toBasic(User user);

    UserDTO toDTO(User user);

    @Mapping(target = "userPhoto", source = "photo")
    UserFullDTO toFull(User user);

    @Mapping(target = "profilePhotoUrl", source = "photo.url")
    UserPublicDTO toPublic(User user);

    UserServiceDTO toService(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UserDTO dto, @MappingTarget User entity);
}

