package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.booking.BookingListDTO;
import com.fran.jobsy.app.dto.booking.BookingResponseDTO;
import com.fran.jobsy.app.entity.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, OfferingMapper.class})
public interface BookingMapper {

    @Mapping(target = "providerName", source = "provider.firstname")
    @Mapping(target = "clientName", source = "client.firstname")
    @Mapping(target = "offeringTitle", source = "offering.title")
    BookingListDTO toBookingListDTO(Booking booking);

    @Mapping(target = "bookingStatus", source = "status")
    BookingResponseDTO toBookingResponseDTO(Booking booking);
}
