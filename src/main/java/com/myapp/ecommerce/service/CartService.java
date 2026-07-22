package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.CartRequest;
import com.myapp.ecommerce.dto.response.CartResponse;

import java.util.UUID;

public interface CartService {

    CartResponse getCartByUser();

    CartResponse handleAddProductToCart(CartRequest request);

    CartResponse handleChangeQuantityInCart(CartRequest request);

    CartResponse handleRemoveCartDetail(UUID id);
}
