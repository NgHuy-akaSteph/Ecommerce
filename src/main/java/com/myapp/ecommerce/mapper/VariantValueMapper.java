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

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface VariantValueMapper {

    default VariantValueResponse toResponse(VariantValue value) {
        if (value == null) return null;
        VariantOptionResponse optionResp = null;
        if (value.getOption() != null) {
            UUID optionId = value.getOption().getId();
            optionResp = VariantOptionResponse.builder()
                    .id(optionId == null ? null : optionId.toString())
                    .name(value.getOption().getName())
                    .code(value.getOption().getCode())
                    .build();
        }
        UUID valueId = value.getId();
        return VariantValueResponse.builder()
                .id(valueId == null ? null : valueId.toString())
                .value(value.getValue())
                .code(value.getCode())
                .option(optionResp)
                .build();
    }

    VariantValue toEntity(VariantValueRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget VariantValue value, VariantValueRequest request);
}
