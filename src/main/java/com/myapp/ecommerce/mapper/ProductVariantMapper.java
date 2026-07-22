package com.myapp.ecommerce.mapper;

import com.myapp.ecommerce.dto.request.ProductVariantRequest;
import com.myapp.ecommerce.dto.response.ProductVariantResponse;
import com.myapp.ecommerce.entity.ProductVariant;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductVariantMapper {

    @Mapping(source = "product.id", target = "productId")
    ProductVariantResponse toResponse(ProductVariant variant);

    @Mapping(target = "product", ignore = true)
    @Mapping(target = "variantValues", ignore = true)
    ProductVariant toEntity(ProductVariantRequest request);

    @Mapping(target = "product", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget ProductVariant variant, ProductVariantRequest request);
}
