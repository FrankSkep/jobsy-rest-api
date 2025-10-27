package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.notification.NotificationResponse;
import com.fran.jobsy.app.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toDTO(Notification notification);
}
