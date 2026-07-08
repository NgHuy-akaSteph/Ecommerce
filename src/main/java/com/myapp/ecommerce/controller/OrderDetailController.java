package com.myapp.ecommerce.controller;


import com.myapp.ecommerce.dto.request.OrderDetailRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.dto.response.OrderDetailResponse;
import com.myapp.ecommerce.entity.OrderDetail;
import com.myapp.ecommerce.service.OrderDetailService;
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

@RestController
@Tag(name = "Orders", description = "Order placement and management")
@RequestMapping("/orderdetails")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderDetailController {

    OrderDetailService orderDetailService;

    @PostMapping
    ResponseEntity<ApiResponse<OrderDetailResponse>> create(@RequestBody @Valid OrderDetailRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Create order detail successfully", orderDetailService.create(request)));
    }

    @GetMapping
    ResponseEntity<ApiResponse<ApiPagination<OrderDetailResponse>>> getAll(@Filter Specification<OrderDetail> spec,
                                                                          Pageable pageable) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get all order details successfully", orderDetailService.getAll(spec, pageable)));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<OrderDetailResponse>> getOrderDetail(@PathVariable("id") String id) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get a order detail successfully", orderDetailService.getById(id)));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<OrderDetailResponse>> update(@PathVariable("id") String id,
                                                           @RequestBody @Valid OrderDetailRequest request) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Update a order detail successfully", orderDetailService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> delete(@PathVariable("id") String id) {
        orderDetailService.delete(id);
        return ResponseEntity.ok().body(ApiResponse.ok("Delete a order detail successfully", "success"));
    }
}