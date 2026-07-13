package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.request.ProductVariantRequest;
import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.dto.response.ProductVariantResponse;
import com.myapp.ecommerce.service.ProductVariantService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product-variants")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Variants", description = "Product variants: options, values, SKU, and stock")
public class ProductVariantController {

    ProductVariantService productVariantService;

    @PostMapping
    ResponseEntity<ApiResponse<ProductVariantResponse>> create(@RequestBody @Valid ProductVariantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Create a new product variant", productVariantService.create(request)));
    }

    @GetMapping
    ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getAll() {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get all product variants", productVariantService.getAll()));
    }

    @GetMapping("/product/{productId}")
    ResponseEntity<ApiResponse<List<ProductVariantResponse>>> getByProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get variants by product", productVariantService.getByProductId(productId)));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<ProductVariantResponse>> getDetails(@PathVariable UUID id) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get variant details", productVariantService.getDetails(id)));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<ProductVariantResponse>> update(
            @PathVariable UUID id, @RequestBody @Valid ProductVariantRequest request) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Update product variant", productVariantService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> delete(@PathVariable UUID id) {
        productVariantService.delete(id);
        return ResponseEntity.ok().body(ApiResponse.ok("Delete product variant", "success"));
    }
}
