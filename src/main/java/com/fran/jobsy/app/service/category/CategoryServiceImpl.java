package com.fran.jobsy.app.service.category;

import com.fran.jobsy.app.dto.category.CategoryDTO;
import com.fran.jobsy.app.dto.category.CategoryRequest;
import com.fran.jobsy.app.entity.Category;
import com.fran.jobsy.app.exception.custom.ResourceAlreadyExistsException;
import com.fran.jobsy.app.exception.custom.ResourceNotFoundException;
import com.fran.jobsy.app.mapper.CategoryMapper;
import com.fran.jobsy.app.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDTO> getAll() {
        return categoryRepository.findAllAsDTO();
    }

    @Override
    public CategoryDTO create(CategoryRequest categoryReq) {
        String newCategory = categoryReq.name().toUpperCase();

        if (categoryRepository.existsByName(newCategory)) {
            throw new ResourceAlreadyExistsException("La categoria " + newCategory + " ya existe.");
        }

        Category category = Category.builder()
                .name(newCategory)
                .build();

        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Override
    public CategoryDTO update(Long id, CategoryRequest categoryReq) {
        String newCategory = categoryReq.name().toUpperCase();

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La categoria " + id + " no existe."));

        if (category.getName().equals(newCategory)) {
            throw new ResourceAlreadyExistsException("La categoria " + newCategory + " ya existe.");
        }

        category.setName(newCategory);
        return categoryMapper.toDTO(categoryRepository.save(category));
    }

    @Override
    public void delete(Long id) {
        categoryRepository.deleteById(id);
    }
}
