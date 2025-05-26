//package com.myapp.ecommerce.service;
//
//import com.myapp.ecommerce.dto.request.CartRequest;
//import com.myapp.ecommerce.dto.response.CartResponse;
//import com.myapp.ecommerce.entity.Cart;
//import com.myapp.ecommerce.entity.CartDetail;
//import com.myapp.ecommerce.entity.Product;
//import com.myapp.ecommerce.entity.User;
//import com.myapp.ecommerce.exception.AppException;
//import com.myapp.ecommerce.exception.ErrorCode;
//import com.myapp.ecommerce.mapper.CartMapper;
//import com.myapp.ecommerce.repository.CartRepository;
//import com.myapp.ecommerce.service.impl.CartServiceImpl;
//import com.myapp.ecommerce.util.SecurityUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CartServiceTest {
//
//    @Mock
//    private CartRepository cartRepository;
//
//    @Mock
//    private CartMapper cartMapper;
//
//    @Mock
//    private ProductService productService;
//
//    @Mock
//    private UserService userService;
//
//    @Mock
//    private CartDetailService cartDetailService;
//
//    @Mock
//    private SecurityUtil securityUtil;
//
//    @InjectMocks
//    private CartServiceImpl cartService;
//
//    private User user;
//    private Cart cart;
//    private CartResponse cartResponse;
//    private CartRequest cartRequest;
//    private Product product;
//    private CartDetail cartDetail;
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
//        cartResponse = new CartResponse();
//        cartResponse.setId("1");
//        cartResponse.setTotalPrice(100.0);
//
//        cartRequest = new CartRequest();
//        cartRequest.setProductId("1");
//        cartRequest.setQuantity(1);
//
//        // Mock security context
//        Authentication authentication = mock(Authentication.class);
//        SecurityContext securityContext = mock(SecurityContext.class);
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//        when(securityUtil.getCurrentUser()).thenReturn(user);
//    }
//
//    @Test
//    void getCartByUser_Success() {
//        // Given
//        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.of(cart));
//        when(cartMapper.toCartResponse(any(Cart.class))).thenReturn(cartResponse);
//
//        // When
//        CartResponse result = cartService.getCartByUser();
//
//        // Then
//        assertNotNull(result);
//        assertEquals(cartResponse.getId(), result.getId());
//        assertEquals(cartResponse.getTotalPrice(), result.getTotalPrice());
//    }
//
//    @Test
//    void getCartByUser_CartNotFound_CreatesNewCart() {
//        // Given
//        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.empty());
//        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
//        when(cartMapper.toCartResponse(any(Cart.class))).thenReturn(cartResponse);
//
//        // When
//        CartResponse result = cartService.getCartByUser();
//
//        // Then
//        assertNotNull(result);
//        verify(cartRepository, times(1)).save(any(Cart.class));
//    }
//
//    @Test
//    void handleAddProductToCart_Success() {
//        // Given
//        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.of(cart));
//        when(productService.getProductById(anyString())).thenReturn(product);
//        when(cartDetailService.fetchByCartAndProduct(any(Cart.class), any(Product.class))).thenReturn(null);
//        when(cartDetailService.save(any(CartDetail.class))).thenReturn(cartDetail);
//        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
//        when(cartMapper.toCartResponse(any(Cart.class))).thenReturn(cartResponse);
//
//        // When
//        CartResponse result = cartService.handleAddProductToCart(cartRequest);
//
//        // Then
//        assertNotNull(result);
//        verify(cartDetailService, times(1)).save(any(CartDetail.class));
//    }
//
//    @Test
//    void handleAddProductToCart_ProductNotFound_ThrowsException() {
//        // Given
//        when(productService.getProductById(anyString())).thenThrow(new AppException(ErrorCode.PRODUCT_NOT_FOUND));
//
//        // When & Then
//        AppException exception = assertThrows(AppException.class,
//            () -> cartService.handleAddProductToCart(cartRequest));
//        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    void handleChangeQuantityInCart_Success() {
//        // Given
//        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.of(cart));
//        when(cartDetailService.fetchById(anyString())).thenReturn(cartDetail);
//        when(cartDetailService.save(any(CartDetail.class))).thenReturn(cartDetail);
//        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
//        when(cartMapper.toCartResponse(any(Cart.class))).thenReturn(cartResponse);
//
//        // When
//        CartResponse result = cartService.handleChangeQuantityInCart(cartRequest);
//
//        // Then
//        assertNotNull(result);
//        verify(cartDetailService, times(1)).save(any(CartDetail.class));
//    }
//
//    @Test
//    void handleChangeQuantityInCart_CartDetailNotFound_ThrowsException() {
//        // Given
//        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.of(cart));
//        when(cartDetailService.fetchById(anyString())).thenReturn(null);
//
//        // When & Then
//        AppException exception = assertThrows(AppException.class,
//            () -> cartService.handleChangeQuantityInCart(cartRequest));
//        assertEquals(ErrorCode.CART_DETAIL_NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    void handleRemoveCartDetail_Success() {
//        // Given
//        when(cartRepository.findByUser(any(User.class))).thenReturn(Optional.of(cart));
//        when(cartDetailService.fetchById(anyString())).thenReturn(cartDetail);
//        doNothing().when(cartDetailService).delete(anyString());
//        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
//        when(cartMapper.toCartResponse(any(Cart.class))).thenReturn(cartResponse);
//
//        // When
//        CartResponse result = cartService.handleRemoveCartDetail("1");
//
//        // Then
//        assertNotNull(result);
//        verify(cartDetailService, times(1)).delete(anyString());
//    }
//}