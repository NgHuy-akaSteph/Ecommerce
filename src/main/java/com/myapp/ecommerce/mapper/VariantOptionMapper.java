package com.myapp.ecommerce.mapper;

import com.myapp.ecommerce.dto.request.VariantOptionRequest;
import com.myapp.ecommerce.dto.response.VariantOptionResponse;
import com.myapp.ecommerce.entity.VariantOption;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface VariantOptionMapper {

    VariantOptionResponse toResponse(VariantOption option);

    VariantOption toEntity(VariantOptionRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget VariantOption option, VariantOptionRequest request);
}
