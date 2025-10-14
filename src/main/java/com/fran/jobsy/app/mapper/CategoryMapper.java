package com.fran.jobsy.app.mapper;

import com.fran.jobsy.app.dto.category.CategoryDTO;
import com.fran.jobsy.app.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDto(Category category);
}
