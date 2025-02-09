package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.UserCreationRequest;
import com.myapp.ecommerce.dto.request.UserUpdateRequest;
import com.myapp.ecommerce.dto.response.UserResponse;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;


public interface UserService {

    UserResponse createUser(UserCreationRequest request);

    List<UserResponse> getAllUsers();

    ApiPagination<UserResponse> getAllUsers(Specification<User> spec, Pageable pageable);

    UserResponse getUserById(String userId);

    User getUserByUsername(String username);

    UserResponse update(String userId, UserUpdateRequest request);

    void delete(String userId);

    boolean isExistByUsername(String username);

}
