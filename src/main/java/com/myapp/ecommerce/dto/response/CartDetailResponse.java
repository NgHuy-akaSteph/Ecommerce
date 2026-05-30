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

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonPropertyOrder(alphabetic = true)
public class CartDetailResponse {

    String id;
    long quantity;
    BigDecimal price;
    Product product;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonPropertyOrder(alphabetic = true)
    public static class Product {
        @JsonProperty("_id")
        String id;
        String name;
        BigDecimal price;
        String thumbnail;
        List<String> sliders;
    }
}
