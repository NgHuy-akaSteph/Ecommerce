package com.myapp.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import com.myapp.ecommerce.entity.enums.OrderStatus;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonPropertyOrder(alphabetic = true)
public class OrderResponse {
    @JsonProperty("_id")
    String id;
    BigDecimal totalPrice;
    String reciverName;
    String reciverAddress;
    String reciverPhone;
    OrderStatus status;
    UserResponse user;
    List<OrderDetailResponse> orderDetails;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
}
