package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.availabilityslot.AvailabilitySlotResponse;
import com.fran.jobsy.app.entity.AvailabilitySlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AvailabilitySlotMapper {
    @Mapping(target = "providerId", source = "user.id")
    AvailabilitySlotResponse toDTO(AvailabilitySlot slot);
}