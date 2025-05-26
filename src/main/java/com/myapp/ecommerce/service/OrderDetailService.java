package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.OrderDetailRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.OrderDetailResponse;
import com.myapp.ecommerce.entity.OrderDetail;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface OrderDetailService {

    OrderDetailResponse create(OrderDetailRequest request);

    OrderDetailResponse update(String id, OrderDetailRequest request);

    OrderDetailResponse getById(String id);

    ApiPagination<OrderDetailResponse> getAll(Specification<OrderDetail> spec, Pageable pageable);

    void delete(String id);
}
