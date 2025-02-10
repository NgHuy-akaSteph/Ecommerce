package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.Cart;
import com.myapp.ecommerce.entity.CartDetail;
import com.myapp.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, String> {

    CartDetail findByCartAndProduct(Cart cart, Product product);
}
