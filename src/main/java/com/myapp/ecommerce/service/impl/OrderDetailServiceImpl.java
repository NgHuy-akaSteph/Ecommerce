package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.dto.request.OrderDetailRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.OrderDetailResponse;
import com.myapp.ecommerce.entity.Order;
import com.myapp.ecommerce.entity.OrderDetail;
import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.mapper.OrderDetailMapper;
import com.myapp.ecommerce.repository.OrderDetailRepository;
import com.myapp.ecommerce.repository.OrderRepository;
import com.myapp.ecommerce.repository.ProductRepository;
import com.myapp.ecommerce.service.OrderDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderDetailServiceImpl implements OrderDetailService {
    OrderDetailRepository orderDetailRepository;
    OrderRepository orderRepository;
    ProductRepository productRepository;
    OrderDetailMapper orderDetailMapper;

    @Override
    @Transactional
    public OrderDetailResponse create(OrderDetailRequest request) {
        log.info("Create a order detail");
        OrderDetail entityDB = orderDetailMapper.toOrderDetail(request);
        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
            entityDB.setProduct(product);
        }

        if(request.getOrderId() != null) {
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
            entityDB.setOrder(order);
        }

        return orderDetailMapper.toOrderDetailResponse(orderDetailRepository.save(entityDB));
    }

    @Override
    @Transactional
    public OrderDetailResponse update(UUID id, OrderDetailRequest request) {
        log.info("Update a order detail");
        OrderDetail entityDB = orderDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        orderDetailMapper.updateOrderDetail(entityDB, request);
        return orderDetailMapper.toOrderDetailResponse(orderDetailRepository.save(entityDB));
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse getById(UUID id) {
        log.info("Get order detail by id");
        OrderDetail entityDB = orderDetailRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return orderDetailMapper.toOrderDetailResponse(entityDB);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiPagination<OrderDetailResponse> getAll(Specification<OrderDetail> spec, Pageable pageable) {
        log.info("Get all order details");
        Page<OrderDetail> page = orderDetailRepository.findAll(spec, pageable);
        List<OrderDetailResponse> list = page.getContent().stream()
                .map(orderDetailMapper::toOrderDetailResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();
        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(page.getTotalElements());
        mt.setPages(page.getTotalPages());

        return ApiPagination.<OrderDetailResponse>builder()
                .meta(mt)
                .result(list)
                .build();
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Delete a order detail");
        orderDetailRepository.deleteById(id);
    }
}
