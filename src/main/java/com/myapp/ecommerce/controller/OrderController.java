package com.myapp.ecommerce.controller;


import com.myapp.ecommerce.dto.request.DeleteAllRequest;
import com.myapp.ecommerce.dto.request.OrderCreationRequest;
import com.myapp.ecommerce.dto.request.OrderUpdateRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ApiString;
import com.myapp.ecommerce.dto.response.OrderResponse;
import com.myapp.ecommerce.entity.Order;
import com.myapp.ecommerce.service.OrderService;
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

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderController {

    OrderService orderService;

    @PostMapping
    @ApiMessage("Create a order successfully")
    ResponseEntity<OrderResponse> create(@RequestBody @Valid OrderCreationRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    @GetMapping
    @ApiMessage("Get all orders successfully")
    ResponseEntity<ApiPagination<OrderResponse>> getAll(@Filter Specification<Order> spec,
                                                        Pageable pageable){
        return ResponseEntity.ok().body(orderService.getAll(spec, pageable));
    }

    @GetMapping("/history")
    @ApiMessage("Get history orders successfully")
    ResponseEntity<ApiPagination<OrderResponse>> getHistory(Pageable pageable){
        return ResponseEntity.ok().body(orderService.getHistory(pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get a order successfully")
    ResponseEntity<OrderResponse> getOrder(@PathVariable("id") String id){
        return ResponseEntity.ok().body(orderService.getById(id));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update a order successfully")
    ResponseEntity<OrderResponse> update(@PathVariable("id") String id,
                                         @RequestBody @Valid OrderUpdateRequest request){
        return ResponseEntity.ok().body(orderService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a order successfully")
    ResponseEntity<ApiString> delete(@PathVariable("id") String id){
        orderService.delete(id);
        return ResponseEntity.ok().body(new ApiString("success"));
    }

    @DeleteMapping("/deleteAll")
    @ApiMessage("Delete list orders successfully")
    ResponseEntity<ApiString> deleteAll(@RequestBody DeleteAllRequest request){
        orderService.deleteAll(request.getIds());
        return ResponseEntity.ok().body(new ApiString("success"));
    }
}
