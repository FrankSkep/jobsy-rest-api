package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.user.*;
import com.fran.jobsy.app.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserPhotoMapper.class, UserWorkPhotoMapper.class})
public interface UserMapper {

    UserBasicDTO toBasic(User user);

    @Mapping(source = "username", target = "email")
    UserDTO toDTO(User user);

    @Mapping(target = "userPhoto", source = "photo")
    @Mapping(source = "username", target = "email")
    UserFullDTO toFull(User user);

    @Mapping(target = "profilePhotoUrl", source = "photo.url")
    UserPublicDTO toPublic(User user);

    UserServiceDTO toService(User user);

    // Mapeo inverso: DTO -> Entity
    @Mapping(source = "email", target = "username")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UserDTO dto, @MappingTarget User entity);
}

