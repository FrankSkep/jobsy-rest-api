package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.category.CategoryDTO;
import com.fran.jobsy.app.dto.category.CategoryRequest;

import java.util.List;

public interface CategoryService {

    List<CategoryDTO> getAll();

    CategoryDTO create(CategoryRequest categoryReq);

    CategoryDTO update(Long id, CategoryRequest categoryReq);

    void delete(Long id);
}
