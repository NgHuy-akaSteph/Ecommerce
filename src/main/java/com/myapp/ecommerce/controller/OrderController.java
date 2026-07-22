package com.myapp.ecommerce.controller;


import com.myapp.ecommerce.dto.request.DeleteAllRequest;
import com.myapp.ecommerce.dto.request.OrderCreationRequest;
import com.myapp.ecommerce.dto.request.OrderUpdateRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.dto.response.OrderResponse;
import com.myapp.ecommerce.entity.Order;
import com.myapp.ecommerce.service.OrderService;
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
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Orders", description = "Order placement and management")
public class OrderController {

    OrderService orderService;

    @PostMapping
    ResponseEntity<ApiResponse<OrderResponse>> create(@RequestBody @Valid OrderCreationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Create a order successfully", orderService.create(request)));
    }

    @GetMapping
    ResponseEntity<ApiResponse<ApiPagination<OrderResponse>>> getAll(@Filter Specification<Order> spec,
                                                                    Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get all orders successfully", orderService.getAll(spec, pageable)));
    }

    @GetMapping("/history")
    ResponseEntity<ApiResponse<ApiPagination<OrderResponse>>> getHistory(Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get history orders successfully", orderService.getHistory(pageable)));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable("id") UUID id) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get a order successfully", orderService.getById(id)));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<OrderResponse>> update(@PathVariable("id") UUID id,
                                                     @RequestBody @Valid OrderUpdateRequest request) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Update a order successfully", orderService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> delete(@PathVariable("id") UUID id) {
        orderService.delete(id);
        return ResponseEntity.ok().body(ApiResponse.ok("Delete a order successfully", "success"));
    }

    @DeleteMapping("/deleteAll")
    ResponseEntity<ApiResponse<String>> deleteAll(@RequestBody DeleteAllRequest request) {
        orderService.deleteAll(request.getIds());
        return ResponseEntity.ok().body(ApiResponse.ok("Delete list orders successfully", "success"));
    }
}