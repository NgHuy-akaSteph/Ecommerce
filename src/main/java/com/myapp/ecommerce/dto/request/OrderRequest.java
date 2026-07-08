package com.myapp.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
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
@Schema(description = "Request payload for creating an order from an existing cart.")
public class OrderRequest {

    @Schema(description = "Order total price; must be >= 1.")
    @Min(value = 1, message = "INVALID_PRICE_ORDER")
    double totalPrice;

    @Schema(description = "Recipient's full name.")
    String reciverName;

    @Schema(description = "Recipient's shipping address.")
    String reciverAddress;

    @Schema(description = "Recipient's phone number.")
    String reciverPhone;

    @Schema(description = "Initial order status.")
    String status;

    @Schema(description = "Id of the user placing the order.")
    String userId;
}
