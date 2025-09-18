package com.fran.jobsy.app.dto.service;

import com.fran.jobsy.app.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceDTO {
    private Long id;
    private UserDTO user;
    private String category;
    private String title;
    private String description;
    private String basePrice;
}
