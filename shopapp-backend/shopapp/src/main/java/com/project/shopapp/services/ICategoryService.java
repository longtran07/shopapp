package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ICategoryService {
    Category createCategory(CategoryDTO categoryDTO);

    Category getCategoryById(Long id);

    List<Category> getAllCategories();

    Category updateCategory(long categoryId,CategoryDTO categoryDTO);

    ResponseEntity<Map<String, String>> deleteCategory(long id);
}
