package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.dto.category.CategoryResponse;
import com.fran.jobsy.app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT new com.fran.jobsy.app.dto.category.CategoryResponse(c.id, c.name) FROM Category c")
    List<CategoryResponse> findAllAsDTO();

    boolean existsByName(String name);
}
