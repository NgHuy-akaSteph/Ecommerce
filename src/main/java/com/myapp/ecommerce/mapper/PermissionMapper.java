package com.myapp.ecommerce.mapper;

import com.myapp.ecommerce.dto.request.PermissionRequest;
import com.myapp.ecommerce.dto.response.PermissionResponse;
import com.myapp.ecommerce.entity.Permission;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePermission(@MappingTarget Permission entity, PermissionRequest request);
}
