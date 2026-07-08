package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.VariantOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VariantOptionRepository extends JpaRepository<VariantOption, String> {

    Optional<VariantOption> findByCode(String code);

    boolean existsByCode(String code);
}
