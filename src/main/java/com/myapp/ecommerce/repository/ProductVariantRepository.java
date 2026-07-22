package com.myapp.ecommerce.repository;

import com.myapp.ecommerce.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {

    List<ProductVariant> findByProductId(UUID productId);

    Optional<ProductVariant> findBySku(String sku);

    boolean existsBySku(String sku);

    @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id = :productId AND pv.deleted = false")
    List<ProductVariant> findActiveByProductId(@Param("productId") UUID productId);

    @Modifying
    @Query("UPDATE ProductVariant pv SET pv.quantity = pv.quantity - :qty WHERE pv.id = :variantId AND pv.quantity >= :qty")
    int decrementQuantity(@Param("variantId") UUID variantId, @Param("qty") long qty);

    @Modifying
    @Query("UPDATE ProductVariant pv SET pv.quantity = pv.quantity + :qty WHERE pv.id = :variantId")
    int incrementQuantity(@Param("variantId") UUID variantId, @Param("qty") long qty);
}
