package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.offering.OfferingMinimalResponse;
import com.fran.jobsy.app.dto.offering.OfferingResponse;
import com.fran.jobsy.app.dto.offering.OfferingSummaryResponse;
import com.fran.jobsy.app.entity.Category;
import com.fran.jobsy.app.entity.Offering;
import com.fran.jobsy.app.entity.OfferingPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface OfferingMapper {

    @Mapping(target = "user", source = "owner")
    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "photoUrls", expression = "java(mapPhotoUrls(offering))")
    @Mapping(target = "isActive", source = "active")
    OfferingResponse toDTO(Offering offering);

    @Mapping(target = "category", source = "category.name")
    @Mapping(target = "photoUrl", expression = "java(getFirstPhotoUrl(offering))")
    @Mapping(target = "isActive", source = "active")
    @Mapping(target = "lat", source = "owner.lat")
    @Mapping(target = "lng", source = "owner.lng")
    OfferingMinimalResponse toMinimalDTO(Offering offering);

    OfferingSummaryResponse toSummaryDTO(Offering offering);

    default String getFirstPhotoUrl(Offering offering) {
        if (offering.getPhotos() != null && !offering.getPhotos().isEmpty()) {
            return offering.getPhotos().get(0).getUrl();
        }
        return null;
    }

    default OfferingResponse toDTOWithStats(Offering offering, Double avgRating, Integer totalReviews) {
        OfferingResponse baseResponse = toDTO(offering);

        return new OfferingResponse(
                baseResponse.id(),
                baseResponse.user(),
                baseResponse.category(),
                baseResponse.title(),
                baseResponse.description(),
                baseResponse.basePrice(),
                baseResponse.yearsOfExperience(),
                baseResponse.isActive(),
                avgRating,
                totalReviews,
                baseResponse.photoUrls()
        );
    }

    default List<String> mapPhotoUrls(Offering offering) {
        if (offering.getPhotos() == null)
            return List.of();
        return offering.getPhotos().stream()
                .map(OfferingPhoto::getUrl)
                .toList();
    }

    default String map(Category category) {
        return category == null ? null : category.getName();
    }
}
