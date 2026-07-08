package com.myapp.ecommerce.dto.request;

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
@Schema(description = "Request payload for an individual order detail line.")
public class OrderDetailRequest {

    @Schema(description = "Number of units in this order detail.")
    long quantity;

    @Schema(description = "Unit price at the time of order.")
    double price;

    @Schema(description = "Id of the product in this order detail.")
    String productId;

    @Schema(description = "Id of the parent order.")
    String orderId;
}
