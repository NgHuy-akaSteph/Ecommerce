package com.myapp.ecommerce.mapper;

import com.myapp.ecommerce.dto.request.UserCreationRequest;
import com.myapp.ecommerce.dto.request.UserRegisterRequest;
import com.myapp.ecommerce.dto.request.UserUpdateRequest;
import com.myapp.ecommerce.dto.response.UserResponse;
import com.myapp.ecommerce.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "role", ignore = true)
    User toUser(UserCreationRequest request);

    @Mapping(target = "role", ignore = true)
    User toUser(UserRegisterRequest request);

    @Mapping(target = "roleName", source = "role.name")
    UserResponse toUserResponse(User user);

    @Mapping(target = "role", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
