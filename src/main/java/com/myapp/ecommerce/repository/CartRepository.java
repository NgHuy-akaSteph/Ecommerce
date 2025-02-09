package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.Cart;
import com.myapp.ecommerce.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    void deleteAll(List<CartDetail> cartDetails);
}
