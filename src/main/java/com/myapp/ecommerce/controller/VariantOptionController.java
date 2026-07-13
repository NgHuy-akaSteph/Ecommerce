package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.request.VariantOptionRequest;
import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.dto.response.VariantOptionResponse;
import com.myapp.ecommerce.service.VariantOptionService;
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
@RequestMapping("/variant-options")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Variants", description = "Product variants: options, values, SKU, and stock")
public class VariantOptionController {

    VariantOptionService variantOptionService;

    @PostMapping
    ResponseEntity<ApiResponse<VariantOptionResponse>> create(@RequestBody @Valid VariantOptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Create a new variant option", variantOptionService.create(request)));
    }

    @GetMapping
    ResponseEntity<ApiResponse<List<VariantOptionResponse>>> getAll() {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get all variant options", variantOptionService.getAll()));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<VariantOptionResponse>> getDetails(@PathVariable UUID id) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get variant option details", variantOptionService.getDetails(id)));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<VariantOptionResponse>> update(
            @PathVariable UUID id, @RequestBody @Valid VariantOptionRequest request) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Update variant option", variantOptionService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> delete(@PathVariable UUID id) {
        variantOptionService.delete(id);
        return ResponseEntity.ok().body(ApiResponse.ok("Delete variant option", "success"));
    }
}
