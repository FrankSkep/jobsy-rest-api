package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.offering.OfferingDTO;
import com.fran.jobsy.app.dto.offering.OfferingSummaryDTO;
import com.fran.jobsy.app.entity.Category;
import com.fran.jobsy.app.entity.Offering;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface OfferingMapper {

    @Mapping(target = "user", source = "owner")
    OfferingDTO toDTO(Offering offering);

    List<OfferingDTO> toDTOList(List<Offering> offerings);

    OfferingSummaryDTO toSummaryDTO(Offering offering);

    default String map(Category category) {
        return category == null ? null : category.getName();
    }
}
