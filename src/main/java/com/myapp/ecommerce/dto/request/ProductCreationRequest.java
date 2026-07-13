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

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request payload for creating a new product.")
public class ProductCreationRequest {

    @Schema(description = "Product name; must be at least 3 characters.")
    @Size(min = 3, message = "INVALID_NAME_PRODUCT")
    String name;

    @Schema(description = "URL of the main product thumbnail image.")
    @NotBlank(message = "INVALID_THUMBNAIL_PRODUCT")
    String thumbnail;

    @Schema(description = "List of URLs for additional product gallery images.")
    List<String> sliders;

    @Schema(description = "Discount amount as decimal; must be >= 0.")
    @Min(value = 0, message = "INVALID_DISCOUNT_PRODUCT")
    BigDecimal discount;

    @Schema(description = "Product price; must be >= 0.")
    @Min(value = 0, message = "INVALID_PRICE_PRODUCT")
    BigDecimal price;

    @Schema(description = "Short description shown in product listings.")
    @NotBlank(message = "INVALID_COLOR_PRODUCT")
    String shortDes;

    @Schema(description = "Id of the parent category.")
    UUID categoryId;

    @Schema(description = "List of tag ids associated with this product.")
    List<UUID> tagsId;

}
