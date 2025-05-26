package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.entity.Cart;
import com.myapp.ecommerce.entity.CartDetail;
import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.dto.request.CartRequest;
import com.myapp.ecommerce.dto.response.CartResponse;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.mapper.CartMapper;
import com.myapp.ecommerce.repository.CartRepository;
import com.myapp.ecommerce.service.CartDetailService;
import com.myapp.ecommerce.service.CartService;
import com.myapp.ecommerce.service.ProductService;
import com.myapp.ecommerce.service.UserService;
import com.myapp.ecommerce.util.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartServiceImpl implements CartService {
    CartRepository cartRepository;
    UserService userService;
    ProductService productService;
    CartDetailService cartDetailService;
    CartMapper cartMapper;

    @Override
    public CartResponse getCartByUser() {
        if(SecurityUtil.getCurrentUserLogin().isPresent()){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String username = SecurityUtil.getCurrentUserLogin().get();
        User user = userService.getUserByUsername(username);
        Cart cart = cartRepository.findByUser(user);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse handleAddProductToCart(CartRequest request) {
        if(SecurityUtil.getCurrentUserLogin().isPresent()){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String username = SecurityUtil.getCurrentUserLogin().get();
        User user = userService.getUserByUsername(username);
        Cart cart = cartRepository.findByUser(user);
        if(cart == null){
            cart = cartRepository.save(Cart.builder().user(user).sum(0).build());
        }
        Product product = productService.getProductById(request.getProductId());
        CartDetail cartDetail = cartDetailService.fetchByCartAndProduct(cart, product);
        List<CartDetail> old = cart.getCartDetails();
        // if product not exist in cart, create new cart detail and add to cart
        if(cartDetail == null){
            cartDetail = CartDetail.builder()
                    .cart(cart)
                    .price(product.getPrice())
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            if(old == null){ // if cart not exist, create new cart
                List<CartDetail> tmp = new ArrayList<>();
                tmp.add(cartDetail);
                old = tmp;
            } else { // if cart exist, add new cart detail to cart
                old.add(cartDetail);
            }
            cart.setCartDetails(old);
            // increase total product in cart
            int total = cart.getSum() + 1;
            cart.setSum(total);
        } else { // if product exist in cart, increase quantity
            long quantity = cartDetail.getQuantity() + request.getQuantity();
            cartDetail.setQuantity(quantity);
        }
        cartDetailService.save(cartDetail);
        cartRepository.save(cart);

        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse handleChangeQuantityInCart(CartRequest request) {
        if(SecurityUtil.getCurrentUserLogin().isPresent()){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String username = SecurityUtil.getCurrentUserLogin().get();
        User user = userService.getUserByUsername(username);
        Cart cart = cartRepository.findByUser(user);
        if(cart == null){
            cart = cartRepository.save(Cart.builder().user(user).sum(0).build());
        }
        Product product = productService.getProductById(request.getProductId());
        CartDetail cartDetail = cartDetailService.fetchByCartAndProduct(cart, product);

        long quantity = request.getQuantity();
        cartDetail.setQuantity(quantity);
        cartDetailService.save(cartDetail);
        cartRepository.save(cart);

        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse handleRemoveCartDetail(String id) {
        CartDetail cartDetail = cartDetailService.fetchById(id);
        Cart cart = cartDetail.getCart();
        if(cart.getSum() > 1){
            int total = cart.getSum() - 1;
            cart.setSum(total);
        } else {
            cart.setSum(0);
        }
        cartRepository.save(cart);
        cartDetailService.delete(id);
        return cartMapper.toCartResponse(cart);
    }
}
