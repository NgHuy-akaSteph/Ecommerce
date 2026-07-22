package com.myapp.ecommerce.service;

import com.myapp.ecommerce.dto.request.UserCreationRequest;
import com.myapp.ecommerce.dto.request.UserUpdateRequest;
import com.myapp.ecommerce.dto.response.UserResponse;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;


public interface UserService {

    UserResponse createUser(UserCreationRequest request);

    List<UserResponse> getAllUsers();

    ApiPagination<UserResponse> getAllUsers(Specification<User> spec, Pageable pageable);

    UserResponse getUserById(UUID userId);

    UserResponse getMyInfo();

    User getUserByUsername(String username);

    UserResponse update(UUID userId, UserUpdateRequest request);

    void delete(UUID userId);

    boolean isExistByUsername(String username);

    void updateUserToken(String refreshToken, String username);

    void handleUserLogout(User user);

    User getUserByUsernameAndRefreshToken(String username, String refreshToken);

    User getUserByUsernameOrEmail(String identifier);

    boolean hasPermission(String username, String path, String httpMethod);
}
