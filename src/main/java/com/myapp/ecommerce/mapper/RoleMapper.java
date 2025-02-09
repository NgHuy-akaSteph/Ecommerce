package com.myapp.ecommerce.mapper;

import com.myapp.ecommerce.dto.request.RoleRequest;
import com.myapp.ecommerce.dto.response.RoleResponse;
import com.myapp.ecommerce.entity.Role;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    Role toRole(RoleRequest roleRequest);

    RoleResponse toRoleResponse(Role role);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRole(@MappingTarget Role role, RoleRequest roleRequest);
}
