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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonPropertyOrder(alphabetic = true)
public class ProductResponse {
    @JsonProperty("_id")
    String id;
    String name;
    String thumbnail;
    BigDecimal discount;
    List<String> sliders;
    BigDecimal price;
    String shortDes;
    List<TagResponse> tags;
    CategoryResponse category;
}
