package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.offering.UserMinimalResponse;
import com.fran.jobsy.app.dto.user.*;
import com.fran.jobsy.app.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserPhotoMapper.class, UserWorkPhotoMapper.class})
public interface UserMapper {
    @Mapping(target = "fullName", expression = "java(user.getFirstname() != null ? user.getFirstname() + \" \" + user.getLastname() : user.getLastname())")
    @Mapping(target = "photoUrl", source = "photo.url")
    UserSummaryResponse toSummaryDTO(User user);

    @Mapping(target = "email", source = "username")
    UserResponse toDTO(User user);

    @Mapping(target = "userPhoto", source = "photo")
    @Mapping(target = "email", source = "username")
    UserFullResponse toFull(User user);

    @Mapping(target = "profilePhotoUrl", source = "photo.url")
    UserPublicDTO toPublic(User user);

    UserMinimalResponse toMinimal(User user);

    @Mapping(target = "username", source = "email")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UserResponse dto, @MappingTarget User entity);
}

