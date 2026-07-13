package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.request.UserCreationRequest;
import com.myapp.ecommerce.dto.request.UserUpdateRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.dto.response.UserResponse;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.service.UserService;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Users", description = "User profile and management")
public class UserController {

    UserService userService;

    @PostMapping
    ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Create a new user", this.userService.createUser(request)));
    }

    @GetMapping
    ResponseEntity<ApiResponse<ApiPagination<UserResponse>>> getUsers(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok("Get all users", this.userService.getAllUsers(spec, pageable)));
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable("id") UUID userId) {
        return ResponseEntity.ok(ApiResponse.ok("Get detail of a user", userService.getUserById(userId)));
    }

    @GetMapping("/my-info")
    ResponseEntity<ApiResponse<UserResponse>> getMyInfo() {
        return ResponseEntity.ok().body(ApiResponse.ok("Get my information", this.userService.getMyInfo()));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<String>> delete(@PathVariable("id") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.ok().body(ApiResponse.ok("Delete a user", "User deleted successfully"));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable("id") UUID userId,
                                                     @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Update a user", userService.update(userId, request)));
    }
}