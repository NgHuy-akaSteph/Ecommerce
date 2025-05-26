package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.Order;
import com.myapp.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByIdIn(List<String> ids);
    Page<Order> findByUser(User user, Pageable pageable);

    Page<Order> findAll(Specification<Order> spec, Pageable pageable);
}
