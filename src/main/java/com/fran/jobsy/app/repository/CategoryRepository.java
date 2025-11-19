package com.fran.jobsy.app.repository;

import com.fran.jobsy.app.dto.category.CategoryDTO;
import com.fran.jobsy.app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT new com.fran.jobsy.app.dto.category.CategoryDTO(c.id, c.name) FROM Category c")
    List<CategoryDTO> findAllAsDTO();

    boolean existsByName(String name);

    @Query("SELECT COUNT(o) > 0 FROM Offering o WHERE o.category.id = :categoryId")
    boolean hasOfferings(@Param("categoryId") Long categoryId);
}
