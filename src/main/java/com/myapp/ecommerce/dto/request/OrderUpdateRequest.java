package com.myapp.ecommerce.dto.request;

import com.myapp.ecommerce.entity.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request payload for updating an order status.")
public class OrderUpdateRequest {

    @Schema(description = "New order status enum value.")
    OrderStatus status;
}
