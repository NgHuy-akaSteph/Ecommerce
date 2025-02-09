package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, String> {
}
