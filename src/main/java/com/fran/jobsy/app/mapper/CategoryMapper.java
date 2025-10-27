package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.category.CategoryResponse;
import com.fran.jobsy.app.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryResponse toDTO(Category category);
}
