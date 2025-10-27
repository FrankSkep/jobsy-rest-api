package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.offering.OfferingResponse;
import com.fran.jobsy.app.dto.offering.OfferingSummaryResponse;
import com.fran.jobsy.app.entity.Category;
import com.fran.jobsy.app.entity.Offering;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface OfferingMapper {

    @Mapping(target = "user", source = "owner")
    OfferingResponse toDTO(Offering offering);

    OfferingSummaryResponse toSummaryDTO(Offering offering);

    default String map(Category category) {
        return category == null ? null : category.getName();
    }
}
