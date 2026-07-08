package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.ProductVariantRequest;
import com.myapp.ecommerce.dto.response.ProductVariantResponse;

import java.util.List;

public interface ProductVariantService {

    ProductVariantResponse create(ProductVariantRequest request);

    ProductVariantResponse update(String variantId, ProductVariantRequest request);

    ProductVariantResponse getDetails(String variantId);

    List<ProductVariantResponse> getByProductId(String productId);

    List<ProductVariantResponse> getAll();

    void delete(String variantId);

    boolean decrementStock(String variantId, long quantity);

    void incrementStock(String variantId, long quantity);
}
