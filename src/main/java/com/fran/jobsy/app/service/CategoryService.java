package com.fran.jobsy.app.service;

import com.fran.jobsy.app.dto.category.CategoryRequest;
import com.fran.jobsy.app.dto.category.CategoryResponse;

import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAll();

    CategoryResponse create(CategoryRequest categoryReq);

    CategoryResponse update(Long id, CategoryRequest categoryReq);

    void delete(Long id);
}
