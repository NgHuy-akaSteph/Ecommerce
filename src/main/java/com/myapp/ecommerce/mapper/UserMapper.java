package com.myapp.ecommerce.mapper;

import com.myapp.ecommerce.dto.request.UserCreationRequest;
import com.myapp.ecommerce.dto.request.UserUpdateRequest;
import com.myapp.ecommerce.dto.response.UserResponse;
import com.myapp.ecommerce.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
//    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "role", ignore = true)
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "role", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
