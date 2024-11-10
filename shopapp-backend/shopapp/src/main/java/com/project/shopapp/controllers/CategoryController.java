package com.project.shopapp.controllers;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.models.Category;
import com.project.shopapp.responses.CategoryResponse;
import com.project.shopapp.responses.UpdateCategoryResponse;
import com.project.shopapp.services.CategoryService;
import com.project.shopapp.utils.MessageKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/categories")
//@Validated
//Dependency injection
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    public ResponseEntity<?> createCategory(
            @Valid  @RequestBody CategoryDTO categoryDTO,
            BindingResult result){
        CategoryResponse categoryResponse=new CategoryResponse();
        if(result.hasErrors()){
            List <String> errorMessage=
                    result.getFieldErrors()
                            .stream()
                            .map(FieldError::getDefaultMessage)
                            .toList();
            categoryResponse.setMessage(localizationUtils.getLocalizedMessage(MessageKeys.CREATE_CATEGORY_FAILED));
            categoryResponse.setErrors(errorMessage);
            return ResponseEntity.badRequest().body(categoryResponse);
        }
        Category category=categoryService.createCategory(categoryDTO);
        categoryResponse.setCategory(category);
        return ResponseEntity.ok(categoryResponse);
    }

    @GetMapping("") // http://localhost:8088/api/v1/categories?page=1&limit=1
    public ResponseEntity<List> getAllCategories(
        @RequestParam("page") int page,
        @RequestParam("limit") int limit
    ){
        List<Category> categories=categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateCategoryResponse> updateAllCategories(
            @PathVariable Long id,
            @Valid @RequestBody
            CategoryDTO categoryDTO,
            HttpServletRequest request
            ){
        categoryService.updateCategory(id,categoryDTO);
        return ResponseEntity.ok(UpdateCategoryResponse.builder()
                .message(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_CATEGORY_SUCCESSFULLY))
                .build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }


}
