package com.myapp.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload for partially updating an existing product.")
public class ProductUpdateRequest {

    @Schema(description = "Updated product name.")
    String name;

    @Schema(description = "Updated thumbnail URL.")
    String thumbnail;

    @Schema(description = "Updated list of gallery image URLs.")
    List<String> sliders;

    @Schema(description = "Updated price.")
    BigDecimal price;

    @Schema(description = "Updated short description.")
    String shortDes;

    @Schema(description = "Updated parent category id.")
    UUID categoryId;

    @Schema(description = "Updated list of tag ids.")
    List<UUID> tagsId;

    @Schema(description = "Updated discount amount as decimal.")
    BigDecimal discount;
}
