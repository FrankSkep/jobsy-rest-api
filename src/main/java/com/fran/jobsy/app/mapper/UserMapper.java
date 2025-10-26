package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.user.*;
import com.fran.jobsy.app.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserPhotoMapper.class, UserWorkPhotoMapper.class})
public interface UserMapper {
    @Mapping(target = "fullName", expression = "java(user.getFirstname() != null ? user.getFirstname() + \" \" + user.getLastname() : user.getLastname())")
    @Mapping(target = "photoUrl", source = "photo.url")
    UserSummaryDTO toSummaryDTO(User user);

    @Mapping(target = "email", source = "username")
    UserDTO toDTO(User user);

    @Mapping(target = "userPhoto", source = "photo")
    @Mapping(target = "email", source = "username")
    UserFullDTO toFull(User user);

    @Mapping(target = "profilePhotoUrl", source = "photo.url")
    UserPublicDTO toPublic(User user);

    UserServiceDTO toService(User user);

    @Mapping(target = "username", source = "email")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UserDTO dto, @MappingTarget User entity);
}

