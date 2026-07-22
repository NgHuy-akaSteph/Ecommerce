package com.myapp.ecommerce.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name="cart_details")
@SQLDelete(sql = "UPDATE cart_details SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class CartDetail extends BaseEntity {

    long quantity;

    BigDecimal price;

    @ManyToOne
    @JoinColumn(name="cart_id")
    @JsonIgnore
    Cart cart;

    @ManyToOne
    @JoinColumn(name="product_id")
    Product product;

    @ManyToOne
    @JoinColumn(name="variant_id", columnDefinition = "uuid")
    ProductVariant variant;
}
