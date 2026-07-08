package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.VariantValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface VariantValueRepository extends JpaRepository<VariantValue, String> {

    List<VariantValue> findByOptionId(String optionId);

    Optional<VariantValue> findByOptionIdAndValue(String optionId, String value);

    @Query("SELECT vv FROM VariantValue vv WHERE vv.id IN :ids")
    List<VariantValue> findAllByIdIn(@Param("ids") Set<String> ids);
}
