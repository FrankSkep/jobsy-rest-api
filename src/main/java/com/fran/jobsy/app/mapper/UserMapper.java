package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.offering.UserMinimalResponse;
import com.fran.jobsy.app.dto.user.*;
import com.fran.jobsy.app.entity.User;
import com.fran.jobsy.app.repository.ReviewRepository;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        uses = {UserPhotoMapper.class, UserWorkPhotoMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    @Mapping(target = "fullName", expression = "java(user.getFirstname() != null ? user.getFirstname() + \" \" + user.getLastname() : user.getLastname())")
    @Mapping(target = "photoUrl", source = "photo.url")
    UserSummaryResponse toSummaryDTO(User user);

    @Mapping(target = "email", source = "username")
    @Mapping(target = "profileImageUrl", source = "photo.url")
    UserResponse toDTO(User user);

    @Mapping(target = "userPhoto", source = "photo")
    @Mapping(target = "email", source = "username")
    UserFullResponse toFull(User user);

    @Mapping(target = "profilePhotoUrl", source = "photo.url")
    @Mapping(target = "averageRating", source = "avgRatingCache")
    @Mapping(target = "totalReviews", expression = "java( Math.toIntExact(reviewRepository.countByProviderId(user.getId())) )")
    UserPublicResponse toPublic(User user, @Context ReviewRepository reviewRepository);

    UserMinimalResponse toMinimal(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateFromDTO(UserPatchRequest dto, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateFromFullDTO(UserFullPatchRequest dto, @MappingTarget User user);
}
