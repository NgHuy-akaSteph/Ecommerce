package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.ProductVariantRequest;
import com.myapp.ecommerce.dto.response.ProductVariantResponse;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ProductVariantService {

    ProductVariantResponse create(ProductVariantRequest request);

    ProductVariantResponse update(UUID variantId, ProductVariantRequest request);

    ProductVariantResponse getDetails(UUID variantId);

    List<ProductVariantResponse> getByProductId(UUID productId);

    List<ProductVariantResponse> getAll();

    void delete(UUID variantId);

    boolean decrementStock(UUID variantId, long quantity);

    void incrementStock(UUID variantId, long quantity);
}
