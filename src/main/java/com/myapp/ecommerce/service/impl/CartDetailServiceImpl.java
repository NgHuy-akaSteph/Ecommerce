package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.entity.Cart;
import com.myapp.ecommerce.entity.CartDetail;
import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.repository.CartDetailRepository;
import com.myapp.ecommerce.service.CartDetailService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartDetailServiceImpl implements CartDetailService {

    CartDetailRepository cartDetailRepository;

    @Override
    public CartDetail fetchByCartAndProduct(Cart cart, Product product) {
        return cartDetailRepository.findByCartAndProduct(cart, product);
    }

    @Override
    public CartDetail getCartDetailById(String cartDetailId) {
        return cartDetailRepository.findById(cartDetailId).
                orElseThrow(() -> new AppException(ErrorCode.CART_DETAIL_NOT_EXISTED));
    }

    @Override
    public CartDetail save(CartDetail cartDetail) {
        return cartDetailRepository.save(cartDetail);
    }

    @Override
    public void delete(String cartDetailId) {
        cartDetailRepository.deleteById(cartDetailId);
    }
}
