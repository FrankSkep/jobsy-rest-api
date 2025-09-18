package com.fran.jobsy.app.dto.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceFilterDTO {
    private Long categoryId;
    private Double minPrice;
    private Double maxPrice;
    private Double minRating;
    private Double lat;
    private Double lng;
    private Double radiusKm;
    private String location;
}