package com.smartpizza.menuservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smartpizza.menuservice.dto.CategoryRequest;
import com.smartpizza.menuservice.dto.CategoryResponse;
import com.smartpizza.menuservice.entity.Category;
import com.smartpizza.menuservice.exception.ResourceNotFoundException;
import com.smartpizza.menuservice.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	@Override
	public CategoryResponse createCategory(CategoryRequest request) {

		log.info("Creating category: {}", request.getCategoryName());

		Category category = new Category();

		category.setCategoryName(request.getCategoryName());
		category.setDescription(request.getDescription());

		Category savedCategory = categoryRepository.save(category);

		log.info("Category created successfully with ID: {}", savedCategory.getCategoryId());

		return convertToResponse(savedCategory);
	}

	@Override
	public List<CategoryResponse> getAllCategories() {

		log.info("Fetching all categories");

		List<Category> categories = categoryRepository.findAll();

		List<CategoryResponse> responses = new ArrayList<>();

		for (Category category : categories) {
			responses.add(convertToResponse(category));
		}

		log.info("Total categories fetched: {}", responses.size());

		return responses;
	}

	@Override
	public CategoryResponse getCategoryById(Long categoryId) {

		log.info("Fetching category with ID: {}", categoryId);

		Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {

			log.error("Category not found with ID: {}", categoryId);

			return new ResourceNotFoundException("Category not found with id: " + categoryId);
		});

		return convertToResponse(category);
	}

	@Override
	public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {

		log.info("Updating category with ID: {}", categoryId);

		Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {

			log.error("Category not found with ID: {}", categoryId);

			return new ResourceNotFoundException("Category not found with id: " + categoryId);
		});

		category.setCategoryName(request.getCategoryName());
		category.setDescription(request.getDescription());

		Category updatedCategory = categoryRepository.save(category);

		log.info("Category updated successfully with ID: {}", updatedCategory.getCategoryId());

		return convertToResponse(updatedCategory);
	}

	@Override
	public void deleteCategory(Long categoryId) {

		log.info("Deleting category with ID: {}", categoryId);

		Category category = categoryRepository.findById(categoryId).orElseThrow(() -> {

			log.error("Category not found with ID: {}", categoryId);

			return new ResourceNotFoundException("Category not found with id: " + categoryId);
		});

		categoryRepository.delete(category);

		log.info("Category deleted successfully with ID: {}", categoryId);
	}

	private CategoryResponse convertToResponse(Category category) {

		CategoryResponse response = new CategoryResponse();

		response.setCategoryId(category.getCategoryId());
		response.setCategoryName(category.getCategoryName());
		response.setDescription(category.getDescription());

		return response;
	}
}