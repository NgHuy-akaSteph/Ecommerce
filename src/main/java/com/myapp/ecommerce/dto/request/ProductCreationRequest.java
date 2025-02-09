package com.myapp.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCreationRequest {

    @Size(min = 3, message = "INVALID_NAME_PRODUCT")
    String name;
    @NotBlank(message = "INVALID_THUMBNAIL_PRODUCT")
    String thumbnail;
    List<String> sliders;
    @Min(value = 0, message = "INVALID_QUANTITY_PRODUCT")
    long quantity;
    @Min(value = 0, message = "INVALID_DISCOUNT_PRODUCT")
    double discount;
    @Min(value = 0, message = "INVALID_PRICE_PRODUCT")
    double price;
    @NotBlank(message = "INVALID_COLOR_PRODUCT")
    String shortDes;
    String categoryId;
    List<String> tagsId;

}
