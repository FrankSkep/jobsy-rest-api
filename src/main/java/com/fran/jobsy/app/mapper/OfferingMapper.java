package com.fran.jobsy.app.mapper;

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

    OfferingSummaryResponse toSummaryDTO(Offering offering);

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
