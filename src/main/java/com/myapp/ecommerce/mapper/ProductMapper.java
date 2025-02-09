package com.myapp.ecommerce.mapper;

import com.myapp.ecommerce.dto.request.ProductCreationRequest;
import com.myapp.ecommerce.dto.request.ProductUpdateRequest;
import com.myapp.ecommerce.dto.response.ProductResponse;
import com.myapp.ecommerce.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductResponse toProductResponse(Product product);

    Product toProduct(ProductCreationRequest request);

    Product toProduct(ProductUpdateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProduct(@MappingTarget Product product, ProductUpdateRequest request);

}
