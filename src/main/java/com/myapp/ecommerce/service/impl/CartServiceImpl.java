package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.entity.Cart;
import com.myapp.ecommerce.entity.CartDetail;
import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.entity.ProductVariant;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.dto.request.CartRequest;
import com.myapp.ecommerce.dto.response.CartResponse;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.mapper.CartMapper;
import com.myapp.ecommerce.repository.CartRepository;
import com.myapp.ecommerce.repository.ProductVariantRepository;
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
    ProductVariantRepository productVariantRepository;
    CartDetailService cartDetailService;
    CartMapper cartMapper;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCartByUser() {
        String username = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        User user = userService.getUserByUsername(username);
        Cart cart = cartRepository.findByUser(user);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse handleAddProductToCart(CartRequest request) {
        String username = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        User user = userService.getUserByUsername(username);
        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            cart = cartRepository.save(Cart.builder().user(user).sum(0).build());
        }
        Product product = productService.getProductById(request.getProductId());

        ProductVariant variant = resolveVariant(request);

        CartDetail cartDetail;
        if (variant != null) {
            cartDetail = cartDetailService.fetchByCartAndProductAndVariant(cart, product, variant);
        } else {
            cartDetail = cartDetailService.fetchByCartAndProduct(cart, product);
        }

        List<CartDetail> old = cart.getCartDetails();
        if (cartDetail == null) {
            if (variant == null) {
                variant = getDefaultVariant(request.getProductId());
            }
            if (variant == null) {
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            if (request.getQuantity() > variant.getQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }
            cartDetail = CartDetail.builder()
                    .cart(cart)
                    .price(variant.getPrice())
                    .product(product)
                    .variant(variant)
                    .quantity(request.getQuantity())
                    .build();
            if (old == null) {
                old = new ArrayList<>();
            }
            old.add(cartDetail);
            cart.setCartDetails(old);
            cart.setSum(cart.getSum() + 1);
        } else {
            ProductVariant targetVariant = cartDetail.getVariant() != null
                    ? cartDetail.getVariant()
                    : getDefaultVariant(request.getProductId());
            if (targetVariant == null) {
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            long totalQty = cartDetail.getQuantity() + request.getQuantity();
            if (totalQty > targetVariant.getQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK);
            }
            cartDetail.setQuantity(totalQty);
        }
        cartDetailService.save(cartDetail);
        cartRepository.save(cart);

        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse handleChangeQuantityInCart(CartRequest request) {
        String username = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        User user = userService.getUserByUsername(username);
        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            cart = cartRepository.save(Cart.builder().user(user).sum(0).build());
        }
        Product product = productService.getProductById(request.getProductId());

        CartDetail cartDetail;
        if (request.getVariantId() != null && !request.getVariantId().isBlank()) {
            ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
            cartDetail = cartDetailService.fetchByCartAndProductAndVariant(cart, product, variant);
        } else {
            cartDetail = cartDetailService.fetchByCartAndProduct(cart, product);
        }

        if (cartDetail == null) {
            throw new AppException(ErrorCode.CART_DETAIL_NOT_EXISTED);
        }

        ProductVariant targetVariant = cartDetail.getVariant() != null
                ? cartDetail.getVariant()
                : getDefaultVariant(request.getProductId());
        if (targetVariant == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        if (request.getQuantity() > targetVariant.getQuantity()) {
            throw new AppException(ErrorCode.OUT_OF_STOCK);
        }
        cartDetail.setQuantity(request.getQuantity());
        cartDetailService.save(cartDetail);
        cartRepository.save(cart);

        return cartMapper.toCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse handleRemoveCartDetail(String id) {
        String username = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
        User user = userService.getUserByUsername(username);

        CartDetail cartDetail = cartDetailService.fetchById(id);
        if (cartDetail == null) {
            throw new AppException(ErrorCode.CART_DETAIL_NOT_EXISTED);
        }

        Cart cart = cartDetail.getCart();
        if (cart == null || !cart.getUser().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.CART_DETAIL_ACCESS_DENIED);
        }

        cart.setSum(Math.max(0, cart.getSum() - 1));
        cartRepository.save(cart);
        cartDetailService.delete(id);
        return cartMapper.toCartResponse(cart);
    }

    private ProductVariant resolveVariant(CartRequest request) {
        if (request.getVariantId() != null && !request.getVariantId().isBlank()) {
            return productVariantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new AppException(ErrorCode.VARIANT_NOT_FOUND));
        }
        return null;
    }

    private ProductVariant getDefaultVariant(String productId) {
        List<ProductVariant> variants = productVariantRepository.findActiveByProductId(productId);
        return variants.isEmpty() ? null : variants.get(0);
    }
}
