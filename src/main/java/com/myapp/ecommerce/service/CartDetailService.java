package com.myapp.ecommerce.service;

import com.myapp.ecommerce.entity.Cart;
import com.myapp.ecommerce.entity.CartDetail;
import com.myapp.ecommerce.entity.Product;

public interface CartDetailService {

    CartDetail fetchByCartAndProduct(Cart cart, Product product);

    CartDetail getCartDetailById(String cartDetailId);

    CartDetail save(CartDetail cartDetail);

    void delete(String cartDetailId);
}
