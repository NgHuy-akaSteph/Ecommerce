package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.VariantValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface VariantValueRepository extends JpaRepository<VariantValue, UUID> {

    List<VariantValue> findByOptionId(UUID optionId);

    Optional<VariantValue> findByOptionIdAndValue(UUID optionId, String value);

    @Query("SELECT vv FROM VariantValue vv WHERE vv.id IN :ids")
    List<VariantValue> findAllByIdIn(@Param("ids") Set<UUID> ids);
}
