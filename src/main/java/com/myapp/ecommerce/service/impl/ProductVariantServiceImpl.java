package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.dto.request.ProductVariantRequest;
import com.myapp.ecommerce.dto.response.ProductVariantResponse;
import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.entity.ProductVariant;
import com.myapp.ecommerce.entity.VariantValue;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.mapper.ProductVariantMapper;
import com.myapp.ecommerce.repository.ProductRepository;
import com.myapp.ecommerce.repository.ProductVariantRepository;
import com.myapp.ecommerce.repository.VariantValueRepository;
import com.myapp.ecommerce.service.ProductVariantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductVariantServiceImpl implements ProductVariantService {

    ProductVariantRepository productVariantRepository;
    ProductRepository productRepository;
    VariantValueRepository variantValueRepository;
    ProductVariantMapper productVariantMapper;

    @Override
    @Transactional
    public ProductVariantResponse create(ProductVariantRequest request) {
        log.info("Create product variant for product: {}", request.getProductId());

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (productVariantRepository.existsBySku(request.getSku())) {
            throw new AppException(ErrorCode.VARIANT_SKU_EXISTED);
        }

        ProductVariant variant = productVariantMapper.toEntity(request);
        variant.setProduct(product);

        if (request.getValueIds() != null && !request.getValueIds().isEmpty()) {
            List<VariantValue> values = variantValueRepository.findAllByIdIn(request.getValueIds());
            variant.setVariantValues(new HashSet<>(values));
        }

        ProductVariant saved = productVariantRepository.save(variant);
        return productVariantMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductVariantResponse update(String variantId, ProductVariantRequest request) {
        log.info("Update product variant: {}", variantId);

        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));

        if (!variant.getSku().equals(request.getSku())
                && productVariantRepository.existsBySku(request.getSku())) {
            throw new AppException(ErrorCode.VARIANT_SKU_EXISTED);
        }

        productVariantMapper.updateEntity(variant, request);

        if (request.getValueIds() != null) {
            List<VariantValue> values = variantValueRepository.findAllByIdIn(request.getValueIds());
            variant.setVariantValues(new HashSet<>(values));
        }

        ProductVariant saved = productVariantRepository.save(variant);
        return productVariantMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantResponse getDetails(String variantId) {
        ProductVariant variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
        return productVariantMapper.toResponse(variant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantResponse> getByProductId(String productId) {
        return productVariantRepository.findActiveByProductId(productId).stream()
                .map(productVariantMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantResponse> getAll() {
        return productVariantRepository.findAll().stream()
                .map(productVariantMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String variantId) {
        log.info("Delete product variant: {}", variantId);
        productVariantRepository.findById(variantId)
                .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
        productVariantRepository.deleteById(variantId);
    }

    @Override
    @Transactional
    public boolean decrementStock(String variantId, long quantity) {
        int updated = productVariantRepository.decrementQuantity(variantId, quantity);
        return updated > 0;
    }

    @Override
    @Transactional
    public void incrementStock(String variantId, long quantity) {
        productVariantRepository.incrementQuantity(variantId, quantity);
    }
}
