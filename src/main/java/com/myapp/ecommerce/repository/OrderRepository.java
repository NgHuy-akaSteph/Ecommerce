package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.Order;
import com.myapp.ecommerce.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
}
