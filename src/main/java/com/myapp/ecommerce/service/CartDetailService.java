package com.myapp.ecommerce.service;

import com.myapp.ecommerce.entity.Cart;
import com.myapp.ecommerce.entity.CartDetail;
import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.entity.ProductVariant;

import java.util.UUID;

public interface CartDetailService {

    CartDetail fetchByCartAndProduct(Cart cart, Product product);

    CartDetail fetchByCartAndProductAndVariant(Cart cart, Product product, ProductVariant variant);

    CartDetail fetchById(UUID cartDetailId);

    CartDetail save(CartDetail cartDetail);

    void delete(UUID cartDetailId);
}
