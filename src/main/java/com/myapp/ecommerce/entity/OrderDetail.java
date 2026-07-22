package com.myapp.ecommerce.entity;


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
@Table(name="order_details")
@SQLDelete(sql = "UPDATE order_details SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class OrderDetail extends BaseEntity {

    long quantity;

    BigDecimal price;

    @ManyToOne
    @JoinColumn(name="order_id")
    Order order;

    @ManyToOne
    @JoinColumn(name="product_id")
    Product product;

    @ManyToOne
    @JoinColumn(name="variant_id", columnDefinition = "uuid")
    ProductVariant variant;
}
