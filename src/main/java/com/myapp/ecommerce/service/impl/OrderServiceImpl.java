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
import com.myapp.ecommerce.entity.ProductVariant;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.entity.enums.OrderStatus;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.mapper.OrderDetailMapper;
import com.myapp.ecommerce.mapper.OrderMapper;
import com.myapp.ecommerce.repository.CartDetailRepository;
import com.myapp.ecommerce.repository.CartRepository;
import com.myapp.ecommerce.repository.OrderDetailRepository;
import com.myapp.ecommerce.repository.OrderRepository;
import com.myapp.ecommerce.repository.ProductRepository;
import com.myapp.ecommerce.repository.ProductVariantRepository;
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

import java.math.BigDecimal;
import java.util.List;
import java.math.RoundingMode;
import java.util.Set;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    ProductVariantRepository productVariantRepository;
    CartRepository cartRepository;
    CartDetailRepository cartDetailRepository;


    @Override
    @Transactional
    public OrderResponse create(OrderCreationRequest request) {
        log.info("Create a order");
        String username = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!user.isEmailVerified()) {
            throw new AppException(ErrorCode.VERIFICATION_REQUIRED);
        }

        List<UUID> detailIds = request.getDetail().stream()
                .map(OrderCreationRequest.DetailRequest::getId).toList();

        Cart cart = cartRepository.findByUser(user);
        if (cart == null || cart.getCartDetails() == null || cart.getCartDetails().isEmpty()) {
            throw new AppException(ErrorCode.CART_DETAIL_NOT_EXISTED);
        }

        Set<UUID> ownedCartDetailIds = cart.getCartDetails().stream()
                .map(cd -> cd.getId())
                .collect(Collectors.toSet());
        for (UUID id : detailIds) {
            if (!ownedCartDetailIds.contains(id)) {
                throw new AppException(ErrorCode.CART_DETAIL_ACCESS_DENIED);
            }
        }

        List<CartDetail> cartDetails = cartDetailRepository.findByIdIn(detailIds);
        if (cartDetails.size() != detailIds.size()) {
            throw new AppException(ErrorCode.CART_DETAIL_NOT_EXISTED);
        }

        Order order = orderMapper.toOrder(request);
        order.setUser(user);

        int newTotal = cart.getSum() - request.getDetail().size();
        cart.setSum(Math.max(0, newTotal));
        cartRepository.save(cart);

        cartDetails.forEach(el -> el.setCart(null));

        Order entityDB = orderRepository.save(order);
        List<OrderDetail> orderDetailList = cartDetails.stream().map(cartDetail -> {
            Product product = productRepository.findById(cartDetail.getProduct().getId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            ProductVariant variant = cartDetail.getVariant();
            if (variant != null) {
                int updated = productVariantRepository.decrementQuantity(variant.getId(), cartDetail.getQuantity());
                if (updated == 0) {
                    throw new AppException(ErrorCode.OUT_OF_STOCK);
                }
            }

            BigDecimal discountPct = product.getDiscount() != null ? product.getDiscount() : BigDecimal.ZERO;
            BigDecimal effectivePrice = cartDetail.getPrice();

            return OrderDetail.builder()
                    .quantity(cartDetail.getQuantity())
                    .price(effectivePrice)
                    .order(entityDB)
                    .product(cartDetail.getProduct())
                    .variant(variant)
                    .build();
        }).toList();

        orderDetailRepository.saveAll(orderDetailList);
        cartDetailRepository.deleteAll(cartDetails);

        List<OrderDetailResponse> orderDetails = orderDetailList.stream()
                .map(orderDetailMapper::toOrderDetailResponse).toList();
        OrderResponse response = orderMapper.toOrderResponse(entityDB);
        response.setOrderDetails(orderDetails);

        return response;
    }

    @Override
    @Transactional
    public OrderResponse update(UUID id, OrderUpdateRequest request) {
        log.info("Update a order");
        String username = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Order entityDB = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!entityDB.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        if (request.getStatus() != null && entityDB.getStatus() != null) {
            validateStatusTransition(entityDB.getStatus(), request.getStatus());
        }

        orderMapper.updateOrder(entityDB, request);
        return orderMapper.toOrderResponse(orderRepository.save(entityDB));
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED -> next == OrderStatus.SHIPPING || next == OrderStatus.CANCELLED;
            case SHIPPING -> next == OrderStatus.DELIVERED;
            case DELIVERED -> next == OrderStatus.CANCELLED;
            case CANCELLED -> false;
        };
        if (!valid) {
            throw new AppException(ErrorCode.INVALID_KEY);
        }
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().map(orderMapper::toOrderResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ApiPagination<OrderResponse> getHistory(Pageable pageable) {
        log.info("Get order history");
        String username = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
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
    @Transactional(readOnly = true)
    public OrderResponse getById(UUID id) {
        log.info("Get a order by id");
        String username = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        return orderMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Cancel a order by id");
        String username = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        if (order.getStatus() != OrderStatus.CANCELLED) {
            order.setStatus(OrderStatus.CANCELLED);
            order.getOrderDetails().forEach(detail -> {
                ProductVariant variant = detail.getVariant();
                if (variant != null) {
                    productVariantRepository.incrementQuantity(variant.getId(), detail.getQuantity());
                }
            });
            orderRepository.save(order);
        }
    }

    @Override
    @Transactional
    public void deleteAll(List<UUID> ids) {
        log.info("Cancel all orders by ids");
        String username = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Order> orders = orderRepository.findByIdIn(ids);
        orders.forEach(order -> {
            if (!order.getUser().getId().equals(user.getId())) {
                return;
            }
            if (order.getStatus() != OrderStatus.CANCELLED) {
                order.setStatus(OrderStatus.CANCELLED);
                order.getOrderDetails().forEach(detail -> {
                    ProductVariant variant = detail.getVariant();
                    if (variant != null) {
                        productVariantRepository.incrementQuantity(variant.getId(), detail.getQuantity());
                    }
                });
                orderRepository.save(order);
            }
        });
    }
}
