package com.fran.jobsy.app.dto.service;

public record ServiceFilterDTO(
        Long categoryId,
        Double minPrice,
        Double maxPrice,
        Double minRating,
        Double lat,
        Double lng,
        Double radiusKm,
        String location
        ) {}