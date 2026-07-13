package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.OrderCreationRequest;
import com.myapp.ecommerce.dto.request.OrderUpdateRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.OrderResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.myapp.ecommerce.entity.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse create(OrderCreationRequest request);
    OrderResponse update(UUID id, OrderUpdateRequest request);
    ApiPagination<OrderResponse> getAll(Specification<Order> spec, Pageable pageable);
    List<OrderResponse> getAll();
    ApiPagination<OrderResponse> getHistory(Pageable pageable);
    OrderResponse getById(UUID id);
    void delete(UUID id);
    void deleteAll(List<UUID> ids);
}
