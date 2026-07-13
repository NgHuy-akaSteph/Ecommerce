package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.request.DeleteAllRequest;
import com.myapp.ecommerce.dto.request.ProductCreationRequest;
import com.myapp.ecommerce.dto.request.ProductUpdateRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.dto.response.ProductResponse;
import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.service.ProductService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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

import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Products", description = "Product CRUD operations")
public class ProductController {
    ProductService productService;

    @PostMapping
    ResponseEntity<ApiResponse<ProductResponse>> create(@RequestBody @Valid ProductCreationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Create a product successfully", productService.create(request)));
    }

    @GetMapping
    ResponseEntity<ApiResponse<ApiPagination<ProductResponse>>> getAllProducts(@Filter Specification<Product> spec, Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get all products successfully", productService.getAll(spec, pageable)));
    }

    @GetMapping("/category/{id}")
    ResponseEntity<ApiResponse<ApiPagination<ProductResponse>>> fetchByCategory(@PathVariable("id") UUID categoryId, Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Fetch products by category successfully", productService.fetchProductsByCategory(categoryId, pageable)));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<ProductResponse>> getDetails(@PathVariable("id") UUID productId) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get product details successfully", productService.getDetails(productId)));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<ProductResponse>> update(@PathVariable("id") UUID productId,
                                                        @RequestBody @Valid ProductUpdateRequest request) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Update product successfully", productService.update(productId, request)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> delete(@PathVariable("id") UUID productId) {
        productService.delete(productId);
        return ResponseEntity.ok().body(ApiResponse.ok("Delete product successfully", "success"));
    }

    @DeleteMapping("/deleteAll")
    ResponseEntity<ApiResponse<String>> deleteListOrder(@RequestBody DeleteAllRequest request) {
        productService.deleteAllById(request.getIds());
        return ResponseEntity.ok().body(ApiResponse.ok("Delete all products successfully", "success"));
    }
}