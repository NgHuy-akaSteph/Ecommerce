package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.Cart;
import com.myapp.ecommerce.entity.CartDetail;
import com.myapp.ecommerce.entity.Product;
import com.myapp.ecommerce.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, String> {

    CartDetail findByCartAndProduct(Cart cart, Product product);

    CartDetail findByCartAndProductAndVariant(Cart cart, Product product, ProductVariant variant);

    List<CartDetail> findByIdIn(List<String> ids);
}
