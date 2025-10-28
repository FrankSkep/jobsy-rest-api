package com.fran.jobsy.app.controller;

import com.fran.jobsy.app.dto.category.CategoryDTO;
import com.fran.jobsy.app.dto.category.CategoryRequest;
import com.fran.jobsy.app.service.category.CategoryService;
import com.fran.jobsy.app.common.UriBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Gestión de categorías de servicios")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Listar categorías", description = "Devuelve la lista de todas las categorías disponibles.")
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Crear categoría", description = "Solo accesible para ADMIN. Permite crear una nueva categoría de servicios.")
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody @Valid CategoryRequest categoryReq) {
        CategoryDTO category = categoryService.create(categoryReq);
        URI location = UriBuilder.buildCreatedLocation(category.id());
        return ResponseEntity.created(location).body(category);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Actualizar categoría", description = "Solo accesible para ADMIN. Permite actualizar una categoría existente.")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @RequestBody @Valid CategoryRequest categoryReq) {
        return ResponseEntity.ok(categoryService.update(id, categoryReq));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Operation(summary = "Eliminar categoría", description = "Solo accesible para ADMIN. Permite eliminar una categoría existente.")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
