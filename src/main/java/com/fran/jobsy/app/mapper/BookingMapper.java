package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.booking.BookingListDTO;
import com.fran.jobsy.app.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "providerName", source = "provider.firstname")
    @Mapping(target = "clientName", source = "client.firstname")
    @Mapping(target = "offeringTitle", source = "offering.title")
    BookingListDTO toBookingListDTO(Booking booking);
}

