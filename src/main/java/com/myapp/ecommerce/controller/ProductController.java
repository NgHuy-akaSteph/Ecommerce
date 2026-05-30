package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.request.DeleteAllRequest;
import com.myapp.ecommerce.dto.request.ProductCreationRequest;
import com.myapp.ecommerce.dto.request.ProductUpdateRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ApiString;
import com.myapp.ecommerce.dto.response.ProductResponse;
import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.service.ProductService;
import com.myapp.ecommerce.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProductController {
    ProductService productService;

    @PostMapping
    @ApiMessage("Create a product successfully")
    ResponseEntity<ProductResponse> create(@RequestBody @Valid ProductCreationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @GetMapping
    @ApiMessage("Get all products successfully")
    ResponseEntity<ApiPagination<ProductResponse>> getAllProducts(@Filter Specification<Product> spec, Pageable pageable){
        return ResponseEntity.ok().body(productService.getAll(spec, pageable));
    }

    @GetMapping("/category/{id}")
    @ApiMessage("Fetch products by category successfully")
    ResponseEntity<ApiPagination<ProductResponse>> fetchByCategory(@PathVariable("id") String categoryId, Pageable pageable){
        return ResponseEntity.ok().body(productService.fetchProductsByCategory(categoryId, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get product details successfully")
    ResponseEntity<ProductResponse> getDetails(@PathVariable("id") String productId){
        return ResponseEntity.ok().body(productService.getDetails(productId));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update product successfully")
    ResponseEntity<ProductResponse> update(@PathVariable("id") String productId,
                                           @RequestBody @Valid ProductUpdateRequest request){
        return ResponseEntity.ok().body(productService.update(productId, request));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete product successfully")
    ResponseEntity<ApiString> delete(@PathVariable("id") String productId){
        productService.delete(productId);
        return ResponseEntity.ok().body(new ApiString("success"));
    }

    @DeleteMapping("/deleteAll")
    @ApiMessage("Delete all products successfully")
    ResponseEntity<ApiString> deleteListOrder(@RequestBody DeleteAllRequest request){
        productService.deleteAllById(request.getIds());
        return ResponseEntity.ok().body(new ApiString("success"));
    }

}
