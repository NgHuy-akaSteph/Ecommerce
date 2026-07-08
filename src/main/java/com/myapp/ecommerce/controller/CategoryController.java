package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.request.CategoryRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.dto.response.CategoryResponse;
import com.myapp.ecommerce.entity.Category;
import com.myapp.ecommerce.service.CategoryService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Categories", description = "Category management")
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    ResponseEntity<ApiResponse<CategoryResponse>> create(@RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Create a category successfully", categoryService.create(request)));
    }

    @GetMapping
    ResponseEntity<ApiResponse<ApiPagination<CategoryResponse>>> getAllCategories(@Filter Specification<Category> spec, Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get all categories successfully", categoryService.getAll(spec, pageable)));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<CategoryResponse>> getDetails(@PathVariable("id") String categoryId) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get category details successfully", categoryService.getDetails(categoryId)));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<CategoryResponse>> update(@PathVariable("id") String categoryId, @RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Update category successfully", categoryService.update(categoryId, request)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> delete(@PathVariable("id") String categoryId) {
        categoryService.delete(categoryId);
        return ResponseEntity.ok().body(ApiResponse.ok("Delete category successfully", "success"));
    }
}