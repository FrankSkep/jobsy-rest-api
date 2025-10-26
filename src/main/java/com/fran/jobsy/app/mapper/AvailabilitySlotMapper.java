package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.availability_slot.AvailabilitySlotDTO;
import com.fran.jobsy.app.entity.AvailabilitySlot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AvailabilitySlotMapper {
    @Mapping(target = "providerId", source = "user.id")
    AvailabilitySlotDTO toDTO(AvailabilitySlot slot);
}