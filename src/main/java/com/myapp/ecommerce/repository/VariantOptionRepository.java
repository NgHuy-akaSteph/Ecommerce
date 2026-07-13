package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.VariantOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface VariantOptionRepository extends JpaRepository<VariantOption, UUID> {

    Optional<VariantOption> findByCode(String code);

    boolean existsByCode(String code);
}
