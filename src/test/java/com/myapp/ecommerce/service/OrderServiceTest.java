//package com.myapp.ecommerce.service;
//
//import com.myapp.ecommerce.dto.request.OrderCreationRequest;
//import com.myapp.ecommerce.dto.request.OrderUpdateRequest;
//import com.myapp.ecommerce.dto.response.ApiPagination;
//import com.myapp.ecommerce.dto.response.OrderDetailResponse;
//import com.myapp.ecommerce.dto.response.OrderResponse;
//import com.myapp.ecommerce.entity.*;
//import com.myapp.ecommerce.exception.AppException;
//import com.myapp.ecommerce.exception.ErrorCode;
//import com.myapp.ecommerce.mapper.OrderDetailMapper;
//import com.myapp.ecommerce.mapper.OrderMapper;
//import com.myapp.ecommerce.repository.*;
//import com.myapp.ecommerce.service.impl.OrderServiceImpl;
//import com.myapp.ecommerce.util.SecurityUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.domain.Specification;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class OrderServiceTest {
//
//    @Mock
//    private OrderRepository orderRepository;
//
//    @Mock
//    private OrderDetailRepository orderDetailRepository;
//
//    @Mock
//    private CartRepository cartRepository;
//
//    @Mock
//    private CartDetailRepository cartDetailRepository;
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private OrderMapper orderMapper;
//
//    @Mock
//    private OrderDetailMapper orderDetailMapper;
//
//    @Mock
//    private SecurityUtil securityUtil;
//
//    @InjectMocks
//    private OrderServiceImpl orderService;
//
//    private User user;
//    private Cart cart;
//    private CartDetail cartDetail;
//    private Product product;
//    private Order order;
//    private OrderDetail orderDetail;
//    private OrderResponse orderResponse;
//    private OrderDetailResponse orderDetailResponse;
//    private OrderCreationRequest orderCreationRequest;
//    private OrderUpdateRequest orderUpdateRequest;
//
//    @BeforeEach
//    void setUp() {
//        user = new User();
//        user.setId("1");
//        user.setUsername("testuser");
//
//        product = new Product();
//        product.setId("1");
//        product.setName("Test Product");
//        product.setPrice(100.0);
//
//        cartDetail = new CartDetail();
//        cartDetail.setId("1");
//        cartDetail.setProduct(product);
//        cartDetail.setQuantity(1);
//
//        cart = new Cart();
//        cart.setId("1");
//        cart.setUser(user);
//        cart.setCartDetails(new ArrayList<>(List.of(cartDetail)));
//
//        orderDetail = new OrderDetail();
//        orderDetail.setId("1");
//        orderDetail.setProduct(product);
//        orderDetail.setQuantity(1);
//        orderDetail.setPrice(100.0);
//
//        order = new Order();
//        order.setId("1");
//        order.setUser(user);
//        order.setOrderDetails(new ArrayList<>(List.of(orderDetail)));
//        order.setTotalPrice(100.0);
//
//        orderResponse = new OrderResponse();
//        orderResponse.setId("1");
//        orderResponse.setTotalPrice(100.0);
//
//        orderDetailResponse = new OrderDetailResponse();
//        orderDetailResponse.setId("1");
//        orderDetailResponse.setProductId("1");
//        orderDetailResponse.setQuantity(1);
//        orderDetailResponse.setPrice(100.0);
//
//        orderCreationRequest = new OrderCreationRequest();
//        orderCreationRequest.setCartId("1");
//
//        orderUpdateRequest = new OrderUpdateRequest();
//        orderUpdateRequest.setStatus("PROCESSING");
//
//        // Mock security context
//        when(securityUtil.getCurrentUser()).thenReturn(user);
//    }
//
//    @Test
//    void createOrder_Success() {
//        // Given
//        when(cartRepository.findById(anyString())).thenReturn(Optional.of(cart));
//        when(cartDetailRepository.findByCart(any(Cart.class))).thenReturn(List.of(cartDetail));
//        when(productRepository.findById(anyString())).thenReturn(Optional.of(product));
//        when(orderMapper.toOrder(any(OrderCreationRequest.class))).thenReturn(order);
//        when(orderRepository.save(any(Order.class))).thenReturn(order);
//        when(orderMapper.toOrderResponse(any(Order.class))).thenReturn(orderResponse);
//        when(orderDetailMapper.toOrderDetailResponse(any(OrderDetail.class))).thenReturn(orderDetailResponse);
//
//        // When
//        OrderResponse result = orderService.create(orderCreationRequest);
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderResponse.getId(), result.getId());
//        verify(orderRepository, times(1)).save(any(Order.class));
//    }
//
//    @Test
//    void createOrder_CartNotFound_ThrowsException() {
//        // Given
//        when(cartRepository.findById(anyString())).thenReturn(Optional.empty());
//
//        // When & Then
//        AppException exception = assertThrows(AppException.class,
//            () -> orderService.create(orderCreationRequest));
//        assertEquals(ErrorCode.CART_NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    void updateOrder_Success() {
//        // Given
//        when(orderRepository.findById(anyString())).thenReturn(Optional.of(order));
//        when(orderRepository.save(any(Order.class))).thenReturn(order);
//        when(orderMapper.toOrderResponse(any(Order.class))).thenReturn(orderResponse);
//
//        // When
//        OrderResponse result = orderService.update("1", orderUpdateRequest);
//
//        // Then
//        assertNotNull(result);
//        verify(orderRepository, times(1)).save(any(Order.class));
//    }
//
//    @Test
//    void updateOrder_NotFound_ThrowsException() {
//        // Given
//        when(orderRepository.findById(anyString())).thenReturn(Optional.empty());
//
//        // When & Then
//        AppException exception = assertThrows(AppException.class,
//            () -> orderService.update("1", orderUpdateRequest));
//        assertEquals(ErrorCode.ORDER_NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    void getOrderById_Success() {
//        // Given
//        when(orderRepository.findById(anyString())).thenReturn(Optional.of(order));
//        when(orderMapper.toOrderResponse(any(Order.class))).thenReturn(orderResponse);
//
//        // When
//        OrderResponse result = orderService.getById("1");
//
//        // Then
//        assertNotNull(result);
//        assertEquals(orderResponse.getId(), result.getId());
//    }
//
//    @Test
//    void getAllOrders_Success() {
//        // Given
//        List<Order> orders = Arrays.asList(order);
//        Page<Order> orderPage = new PageImpl<>(orders);
//        when(orderRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(orderPage);
//        when(orderMapper.toOrderResponse(any(Order.class))).thenReturn(orderResponse);
//
//        // When
//        ApiPagination<OrderResponse> result = orderService.getAll(mock(Specification.class), mock(Pageable.class));
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.getResult().size());
//    }
//
//    @Test
//    void getOrderHistory_Success() {
//        // Given
//        List<Order> orders = Arrays.asList(order);
//        Page<Order> orderPage = new PageImpl<>(orders);
//        when(orderRepository.findByUser(any(User.class), any(Pageable.class))).thenReturn(orderPage);
//        when(orderMapper.toOrderResponse(any(Order.class))).thenReturn(orderResponse);
//
//        // When
//        ApiPagination<OrderResponse> result = orderService.getHistory(mock(Pageable.class));
//
//        // Then
//        assertNotNull(result);
//        assertEquals(1, result.getResult().size());
//    }
//
//    @Test
//    void deleteOrder_Success() {
//        // Given
//        when(orderRepository.findById(anyString())).thenReturn(Optional.of(order));
//        doNothing().when(orderRepository).delete(any(Order.class));
//
//        // When
//        orderService.delete("1");
//
//        // Then
//        verify(orderRepository, times(1)).delete(any(Order.class));
//    }
//
//    @Test
//    void deleteAllOrders_Success() {
//        // Given
//        List<String> orderIds = Arrays.asList("1", "2");
//        when(orderRepository.findAllById(anyList())).thenReturn(Arrays.asList(order));
//        doNothing().when(orderRepository).deleteAll(anyList());
//
//        // When
//        orderService.deleteAll(orderIds);
//
//        // Then
//        verify(orderRepository, times(1)).deleteAll(anyList());
//    }
//}