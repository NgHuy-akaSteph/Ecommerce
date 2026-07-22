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
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonPropertyOrder(alphabetic = true)
public class ProductVariantResponse {

    @JsonProperty("_id")
    String id;

    String productId;
    String sku;
    BigDecimal price;
    BigDecimal compareAtPrice;
    long quantity;
    Set<VariantValueResponse> values;
    List<String> images;
}
