package com.myapp.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

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
    double totalPrice;
    String reciverName;
    String reciverAddress;
    String reciverPhone;
    String status;
    UserResponse user;
    List<OrderDetailResponse> orderDetails;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;
}
