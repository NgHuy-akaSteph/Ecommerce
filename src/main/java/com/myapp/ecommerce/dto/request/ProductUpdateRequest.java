package com.myapp.ecommerce.dto.request;

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
public class ProductUpdateRequest {

    String name;
    String thumbnail;
    List<String> sliders;
    BigDecimal price;
    String shortDes;
    String categoryId;
    List<String> tagsId;
    double quantity;
    BigDecimal discount;
}
