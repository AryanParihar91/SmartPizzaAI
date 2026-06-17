package com.smartpizza.menuservice.service;

import com.smartpizza.menuservice.dto.CategoryRequest;
import com.smartpizza.menuservice.dto.CategoryResponse;
import com.smartpizza.menuservice.entity.Category;
import com.smartpizza.menuservice.exception.ResourceNotFoundException;
import com.smartpizza.menuservice.repository.CategoryRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category category;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setup() {
        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Veg Pizza");
        category.setDescription("Vegetarian pizza category");

        categoryRequest = new CategoryRequest();
        categoryRequest.setCategoryName("Veg Pizza");
        categoryRequest.setDescription("Vegetarian pizza category");
    }

    @Test
    void createCategoryShouldReturnCategoryResponse() {

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryResponse response = categoryService.createCategory(categoryRequest);

        assertNotNull(response);
        assertEquals(1L, response.getCategoryId());
        assertEquals("Veg Pizza", response.getCategoryName());
        assertEquals("Vegetarian pizza category", response.getDescription());

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void getAllCategoriesShouldReturnCategoryList() {

        Category category2 = new Category();
        category2.setCategoryId(2L);
        category2.setCategoryName("Non Veg Pizza");
        category2.setDescription("Non vegetarian pizza category");

        when(categoryRepository.findAll()).thenReturn(Arrays.asList(category, category2));

        List<CategoryResponse> responses = categoryService.getAllCategories();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Veg Pizza", responses.get(0).getCategoryName());
        assertEquals("Non Veg Pizza", responses.get(1).getCategoryName());

        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoryByIdWhenCategoryExistsShouldReturnCategory() {

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryResponse response = categoryService.getCategoryById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getCategoryId());
        assertEquals("Veg Pizza", response.getCategoryName());

        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void getCategoryByIdWhenCategoryNotFoundShouldThrowException() {

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryById(99L);
        });

        verify(categoryRepository, times(1)).findById(99L);
    }

    @Test
    void updateCategoryShouldReturnUpdatedCategory() {

        CategoryRequest updateRequest = new CategoryRequest();
        updateRequest.setCategoryName("Updated Veg Pizza");
        updateRequest.setDescription("Updated description");

        Category updatedCategory = new Category();
        updatedCategory.setCategoryId(1L);
        updatedCategory.setCategoryName("Updated Veg Pizza");
        updatedCategory.setDescription("Updated description");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        CategoryResponse response = categoryService.updateCategory(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Updated Veg Pizza", response.getCategoryName());
        assertEquals("Updated description", response.getDescription());

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void deleteCategoryShouldDeleteCategory() {

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).delete(category);
    }
}