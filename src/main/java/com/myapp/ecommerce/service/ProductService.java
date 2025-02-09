package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.ProductCreationRequest;
import com.myapp.ecommerce.dto.request.ProductUpdateRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ProductResponse;
import com.myapp.ecommerce.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


import java.util.List;

public interface ProductService {

    ProductResponse create(ProductCreationRequest request);

    ProductResponse update(String productId, ProductUpdateRequest request);

    ProductResponse getDetails (String productId);

    List<ProductResponse> getAll();

    ApiPagination<ProductResponse> getAll(Specification<Product> spec, Pageable pageable);

    void delete(String productId);

    ApiPagination<ProductResponse> fetchProductsByCategory(String categoryId, Pageable pageable);

    void save(Product product);

    void deleteAll(List<Product> products);

    void deleteAllById(List<String> productIds);
}
