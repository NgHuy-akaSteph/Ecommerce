package com.myapp.ecommerce.mapper;

import com.myapp.ecommerce.dto.request.VariantValueRequest;
import com.myapp.ecommerce.dto.response.VariantOptionResponse;
import com.myapp.ecommerce.dto.response.VariantValueResponse;
import com.myapp.ecommerce.entity.VariantValue;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface VariantValueMapper {

    default VariantValueResponse toResponse(VariantValue value) {
        if (value == null) return null;
        VariantOptionResponse optionResp = null;
        if (value.getOption() != null) {
            optionResp = VariantOptionResponse.builder()
                    .id(value.getOption().getId())
                    .name(value.getOption().getName())
                    .code(value.getOption().getCode())
                    .build();
        }
        return VariantValueResponse.builder()
                .id(value.getId())
                .value(value.getValue())
                .code(value.getCode())
                .option(optionResp)
                .build();
    }

    VariantValue toEntity(VariantValueRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget VariantValue value, VariantValueRequest request);
}
