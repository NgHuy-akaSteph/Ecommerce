package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    Page<Permission> findAll(Specification<Permission> spec, Pageable pageable);
}
