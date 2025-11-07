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
    @Mapping(target = "averageRating", expression = "java(calculateAverageRating(offering))")
    @Mapping(target = "totalReviews", expression = "java(calculateTotalReviews(offering))")
    @Mapping(target = "photoUrls", expression = "java(mapPhotoUrls(offering))")
    @Mapping(target = "isActive", source = "active")
    OfferingResponse toDTO(Offering offering);

    OfferingSummaryResponse toSummaryDTO(Offering offering);

    default Double calculateAverageRating(Offering offering) {
        return offering.getOwner().getAvgRatingCache();
    }

    default Integer calculateTotalReviews(Offering offering) {
        // Retorna 0 por defecto, se calculará en el servicio
        return 0;
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
