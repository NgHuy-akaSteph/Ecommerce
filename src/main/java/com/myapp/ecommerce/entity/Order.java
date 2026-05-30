package com.myapp.ecommerce.entity;


import com.myapp.ecommerce.entity.enums.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name="orders")
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id = ?")
@SQLRestriction("deleted = false")
public class Order extends BaseEntity {

    BigDecimal totalPrice;
    String customerName;
    String customerAddress;
    String customerPhone;
    
    @Enumerated(EnumType.STRING)
    OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = "order")
    List<OrderDetail> orderDetails;
}
