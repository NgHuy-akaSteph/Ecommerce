package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.dto.request.OrderCreationRequest;
import com.myapp.ecommerce.dto.request.OrderUpdateRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.OrderDetailResponse;
import com.myapp.ecommerce.dto.response.OrderResponse;
import com.myapp.ecommerce.entity.Cart;
import com.myapp.ecommerce.entity.CartDetail;
import com.myapp.ecommerce.entity.Order;
import com.myapp.ecommerce.entity.OrderDetail;
import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.mapper.OrderDetailMapper;
import com.myapp.ecommerce.mapper.OrderMapper;
import com.myapp.ecommerce.repository.CartDetailRepository;
import com.myapp.ecommerce.repository.CartRepository;
import com.myapp.ecommerce.repository.OrderDetailRepository;
import com.myapp.ecommerce.repository.OrderRepository;
import com.myapp.ecommerce.repository.ProductRepository;
import com.myapp.ecommerce.repository.UserRepository;
import com.myapp.ecommerce.service.OrderService;
import com.myapp.ecommerce.util.SecurityUtil;
import com.turkraft.springfilter.boot.Filter;
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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class OrderServiceImpl implements OrderService {

    OrderMapper orderMapper;
    OrderDetailMapper orderDetailMapper;
    OrderRepository orderRepository;
    UserRepository userRepository;
    OrderDetailRepository orderDetailRepository;
    ProductRepository productRepository;
    CartRepository cartRepository;
    CartDetailRepository cartDetailRepository;



    @Override
    @Transactional
    public OrderResponse create(OrderCreationRequest request) {
        log.info("Create a order");
        Order order = orderMapper.toOrder(request);
        List<String> ids = request.getDetail().stream()
                .map(OrderCreationRequest.DetailRequest::getId).toList();
        List<CartDetail> cartDetails = cartDetailRepository.findByIdIn(ids);

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        order.setUser(user);
        //Buy product in cart and update quantity
        Cart cart = cartRepository.findByUser(user);
        int newTotal = cart.getSum() - request.getDetail().size();
        cart.setSum(newTotal);
        cartRepository.save(cart);
        cartDetails.forEach(el -> {
            el.setCart(null);
            cartDetailRepository.save(el);
        });
        // Create order
        Order entityDB = orderRepository.save(order);
        List<OrderDetail> orderDetailList = request.getDetail().stream()
                .map(item -> {
                    // Get product from cart detail
                    CartDetail cartDetail = cartDetailRepository.findById(item.getId())
                            .orElseThrow(() -> new AppException(ErrorCode.CART_DETAIL_NOT_EXISTED));
                    Product product = cartDetail.getProduct();
                    // Update quantity of product
                    long newQuantity = product.getQuantity() - cartDetail.getQuantity();
                    product.setQuantity(newQuantity);
                    productRepository.save(product);
                    // Create order detail
                    OrderDetail orderDetail = OrderDetail.builder()
                            .quantity(cartDetail.getQuantity())
                            .price(product.getPrice())
                            .order(entityDB)
                            .product(product)
                            .build();
                    return orderDetailRepository.save(orderDetail);
                }).toList();
        // delete cart detail after buy
        cartDetailRepository.deleteAll(cartDetails);

        // Create order response
        List<OrderDetailResponse> orderDetails = orderDetailList.stream()
                .map(orderDetailMapper::toOrderDetailResponse).toList();
        OrderResponse response = orderMapper.toOrderResponse(entityDB);
        response.setOrderDetails(orderDetails);

        return response;
    }

    @Override
    @Transactional
    public OrderResponse update(String id, OrderUpdateRequest request) {
        log.info("Update a order");

        Order entityDB = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        orderMapper.updateOrder(entityDB, request);
        return orderMapper.toOrderResponse(orderRepository.save(entityDB));
    }

    @Override
    public ApiPagination<OrderResponse> getAll(@Filter Specification<Order> spec, Pageable pageable) {
        log.info("Get all orders");
        Page<Order> pageOrder = orderRepository.findAll(spec, pageable);
        List<OrderResponse> list = pageOrder.getContent().stream()
                .map(orderMapper::toOrderResponse).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();
        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageOrder.getTotalPages());
        mt.setTotal(pageOrder.getTotalElements());
        
        return ApiPagination.<OrderResponse>builder()
                .meta(mt)
                .result(list)
                .build();
    }

    @Override
    public List<OrderResponse> getAll() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(orderMapper::toOrderResponse).toList();
    }

    @Override
    public ApiPagination<OrderResponse> getHistory(Pageable pageable) {
        log.info("Get order history");
        if (SecurityUtil.getCurrentUserLogin().isPresent()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String username = SecurityUtil.getCurrentUserLogin().get();
        User user = this.userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Page<Order> pageOrder = this.orderRepository.findByUser(user, pageable);

        List<OrderResponse> list = pageOrder.getContent().stream()
                .map((item) -> {
                    OrderResponse res = this.orderMapper.toOrderResponse(item);
                    List<OrderDetailResponse> orderDetails = item.getOrderDetails().stream()
                            .map(orderDetailMapper::toOrderDetailResponse).toList();
                    res.setOrderDetails(orderDetails);
                    return res;
                }).toList();

        ApiPagination.Meta mt = new ApiPagination.Meta();

        mt.setCurrent(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pageOrder.getTotalPages());
        mt.setTotal(pageOrder.getTotalElements());

        return ApiPagination.<OrderResponse>builder()
                .meta(mt)
                .result(list)
                .build();
    }

    @Override
    public OrderResponse getById(String id) {
        log.info("Get a order by id");
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public void delete(String id) {
        log.info("Delete a order by id");
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        orderDetailRepository.deleteAll(order.getOrderDetails());
        orderRepository.delete(order);
    }

    @Override
    @Transactional
    public void deleteAll(List<String> ids) {
        log.info("Delete all orders by ids");
        List<Order> orders = orderRepository.findByIdIn(ids);
        orders.forEach(order -> orderDetailRepository.deleteAll(order.getOrderDetails()));
        orderRepository.deleteAll(orders);
    }
}
