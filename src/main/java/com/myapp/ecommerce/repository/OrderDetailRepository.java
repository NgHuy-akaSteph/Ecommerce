package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.OrderDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, String> {
    Page<OrderDetail> findAll(Specification<OrderDetail> spec, Pageable pageable);
}
