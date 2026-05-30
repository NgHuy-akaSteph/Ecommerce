package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.request.CategoryRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ApiString;
import com.myapp.ecommerce.dto.response.CategoryResponse;
import com.myapp.ecommerce.entity.Category;
import com.myapp.ecommerce.service.CategoryService;
import com.myapp.ecommerce.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
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
public class CategoryController {
    CategoryService categoryService;

    @PostMapping
    @ApiMessage("Create a category successfully")
    ResponseEntity<CategoryResponse> create(@RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @GetMapping
    @ApiMessage("Get all categories successfully")
    ResponseEntity<ApiPagination<CategoryResponse>> getAllCategories(@Filter Specification<Category> spec, Pageable pageable) {
        return ResponseEntity.ok().body(categoryService.getAll(spec, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get category details successfully")
    ResponseEntity<CategoryResponse> getDetails(@PathVariable("id") String categoryId) {
        return ResponseEntity.ok().body(categoryService.getDetails(categoryId));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update category successfully")
    ResponseEntity<CategoryResponse> update(@PathVariable("id") String categoryId, @RequestBody @Valid CategoryRequest request) {
        return ResponseEntity.ok().body(categoryService.update(categoryId, request));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete category successfully")
    ResponseEntity<ApiString> delete(@PathVariable("id") String categoryId) {
        categoryService.delete(categoryId);
        return ResponseEntity.ok().body(new ApiString("success"));
    }
}
