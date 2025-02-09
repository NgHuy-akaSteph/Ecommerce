package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Page<Role> findAll(Specification<Role> spec, Pageable pageable);

    Optional<Role> findByName(String name);
}
