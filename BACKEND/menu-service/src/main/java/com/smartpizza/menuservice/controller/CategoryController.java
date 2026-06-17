package com.smartpizza.menuservice.controller;

import com.smartpizza.menuservice.dto.CategoryRequest;
import com.smartpizza.menuservice.dto.CategoryResponse;
import com.smartpizza.menuservice.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // creating a pizza category
    @PostMapping
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    // get all pizza categories
    @GetMapping
    public List<CategoryResponse> getAllCategories() {
        return categoryService.getAllCategories();
    }
    
    // get category by Id
    @GetMapping("/{categoryId}")
    public CategoryResponse getCategoryById(@PathVariable Long categoryId) {
        return categoryService.getCategoryById(categoryId);
    }
    
    // update category
    @PutMapping("/{categoryId}")
    public CategoryResponse updateCategory(@PathVariable Long categoryId,
                                           @Valid @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(categoryId, request);
    }
    
    // delete pizza
    @DeleteMapping("/{categoryId}")
    public String deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return "Category deleted successfully";
    }
}