package com.myapp.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request payload for adding or updating a cart line item.")
public class CartRequest {

    @Schema(description = "Id of the product to add or modify in the cart.")
    UUID productId;

    @Schema(description = "Id of the specific variant to add. If not provided, uses the default variant.")
    UUID variantId;

    @Schema(description = "Desired quantity.")
    long quantity;
}
