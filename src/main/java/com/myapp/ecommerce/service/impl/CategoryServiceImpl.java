package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.dto.request.CategoryRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.CategoryResponse;
import com.myapp.ecommerce.entity.Category;
import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.mapper.CategoryMapper;
import com.myapp.ecommerce.repository.CategoryRepository;
import com.myapp.ecommerce.service.CategoryService;
import com.myapp.ecommerce.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    ProductService productService;

    @Override
    public CategoryResponse create(CategoryRequest request) {
        log.info("Create a category");
        if(categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        Category category = categoryMapper.toCategory(request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse update(String categoryId, CategoryRequest request) {
        log.info("Update a category");
        if(categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_EXISTED);
        }
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
//        String oldName = category.getName();
        category.setName(request.getName());
        List<Product> productList = category.getProducts();
        productList.forEach(product -> {
            product.setCategory(category);
            productService.save(product);
        });
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse getDetails(String categoryId) {
        log.info("Get details of a category");

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return categoryMapper.toCategoryResponse(category);
    }

    @Override
    public ApiPagination<CategoryResponse> getAll(Specification<Category> spec, Pageable pageable) {
        log.info("Get all categories");

        Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);
        List<CategoryResponse> list = categoryPage.getContent()
                .stream().map(categoryMapper::toCategoryResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();
        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(categoryPage.getTotalPages());
        mt.setTotal(categoryPage.getTotalElements());

        return ApiPagination.<CategoryResponse>builder()
                .meta(mt)
                .result(list)
                .build();
    }

    @Override
    public void delete(String categoryId) {
        log.info("Delete a category");
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        List<Product> productList = category.getProducts();
        productService.deleteAll(productList);
        categoryRepository.delete(category);
    }
}
