package com.myapp.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonPropertyOrder(alphabetic = true)
public class OrderDetailResponse {

    @JsonProperty("_id")
    String id;
    long quantity;
    BigDecimal price;
    ProductResponse product;
    VariantInfo variant;
    Instant createdAt;
    Instant updatedAt;
    String createdBy;
    String updatedBy;

    @lombok.experimental.FieldDefaults(level = AccessLevel.PRIVATE)
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    @lombok.Data
    @JsonPropertyOrder(alphabetic = true)
    public static class VariantInfo {
        @JsonProperty("_id")
        String id;
        String sku;
        long quantity;
        BigDecimal price;
    }
}
