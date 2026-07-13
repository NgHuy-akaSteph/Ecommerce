package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.CategoryRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.CategoryResponse;
import com.myapp.ecommerce.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(UUID categoryId, CategoryRequest request);

    CategoryResponse getDetails(UUID categoryId);

    ApiPagination<CategoryResponse>  getAll(Specification<Category> spec, Pageable pageable);

    void delete(UUID categoryId);
}
