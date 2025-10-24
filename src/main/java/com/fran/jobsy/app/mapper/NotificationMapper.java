package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.notification.NotificationDTO;
import com.fran.jobsy.app.entity.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationDTO toDTO(Notification notification);
}
