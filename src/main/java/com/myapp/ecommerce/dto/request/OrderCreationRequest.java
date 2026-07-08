package com.myapp.ecommerce.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request payload for creating a new order.")
public class OrderCreationRequest {

    @Schema(description = "Order total price; must be >= 1.")
    @Min(value = 1, message = "INVALID_PRICE_ORDER")
    BigDecimal totalPrice;

    @Schema(description = "Recipient's full name.")
    String reciverName;

    @Schema(description = "Recipient's shipping address.")
    String reciverAddress;

    @Schema(description = "Recipient's phone number; must be 10 digits.")
    @Pattern(regexp = "(^$|[0-9]{10})")
    String reciverPhone;

    @Schema(description = "Initial order status.")
    String status;

    @Schema(description = "Id of the user placing the order.")
    String userId;

    @Schema(description = "List of order detail items.")
    List<DetailRequest> detail;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Schema(description = "Order detail line entry.")
    public static class DetailRequest {

        @Schema(description = "Product id included in this order line.")
        @JsonProperty("_id")
        String id;
    }
}
