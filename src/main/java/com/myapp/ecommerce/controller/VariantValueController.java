package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.request.VariantValueRequest;
import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.dto.response.VariantValueResponse;
import com.myapp.ecommerce.service.VariantValueService;
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

@RestController
@RequestMapping("/variant-values")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Variants", description = "Product variants: options, values, SKU, and stock")
public class VariantValueController {

    VariantValueService variantValueService;

    @PostMapping
    ResponseEntity<ApiResponse<VariantValueResponse>> create(@RequestBody @Valid VariantValueRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Create a new variant value", variantValueService.create(request)));
    }

    @GetMapping
    ResponseEntity<ApiResponse<List<VariantValueResponse>>> getAll() {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get all variant values", variantValueService.getAll()));
    }

    @GetMapping("/option/{optionId}")
    ResponseEntity<ApiResponse<List<VariantValueResponse>>> getByOption(@PathVariable String optionId) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get variant values by option", variantValueService.getByOptionId(optionId)));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<VariantValueResponse>> getDetails(@PathVariable String id) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get variant value details", variantValueService.getDetails(id)));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<VariantValueResponse>> update(
            @PathVariable String id, @RequestBody @Valid VariantValueRequest request) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Update variant value", variantValueService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> delete(@PathVariable String id) {
        variantValueService.delete(id);
        return ResponseEntity.ok().body(ApiResponse.ok("Delete variant value", "success"));
    }
}
