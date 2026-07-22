package com.myapp.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request payload for creating or updating a product variant.")
public class ProductVariantRequest {

    @Schema(description = "ID of the parent product. Required for creation.")
    UUID productId;

    @Schema(description = "Unique SKU for this variant.")
    @NotBlank(message = "INVALID_SKU")
    @Size(min = 1, max = 100, message = "INVALID_SKU")
    String sku;

    @Schema(description = "Variant price; must be >= 0.")
    @Min(value = 0, message = "INVALID_PRICE")
    BigDecimal price;

    @Schema(description = "Compare-at price (original price before discount).")
    @Min(value = 0, message = "INVALID_PRICE")
    BigDecimal compareAtPrice;

    @Schema(description = "Stock quantity for this variant; must be >= 0.")
    @Min(value = 0, message = "INVALID_QUANTITY")
    long quantity;

    @Schema(description = "Set of value IDs that define this variant's options.")
    Set<UUID> valueIds;
}
