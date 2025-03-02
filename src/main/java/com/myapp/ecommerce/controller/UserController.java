package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.request.UserCreationRequest;
import com.myapp.ecommerce.dto.request.UserUpdateRequest;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.dto.response.ApiString;
import com.myapp.ecommerce.dto.response.UserResponse;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.service.UserService;
import com.myapp.ecommerce.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    UserService userService;

    @PostMapping
    @ApiMessage("Create a new user")
    ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.createUser(request));
    }

    @GetMapping
    @ApiMessage("Get all users")
    ResponseEntity<ApiPagination<UserResponse>> getUsers(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.ok(this.userService.getAllUsers(spec, pageable));
    }

    @GetMapping("/{id}")
    @ApiMessage("Get detail of a user")
    UserResponse getUser(@PathVariable("id") String userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/my-info")
    @ApiMessage("Get my information")
    ResponseEntity<UserResponse> getMyInfo() {
        return ResponseEntity.ok().body(this.userService.getMyInfo());
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a user")
    ResponseEntity<ApiString> delete(@PathVariable("id") String userId) {
        userService.delete(userId);
        return ResponseEntity.ok().body(new ApiString("User deleted successfully"));
    }

    @PutMapping("/{id}")
    @ApiMessage("Update a user")
    ResponseEntity<UserResponse> update(@PathVariable("id") String userId,
                                        @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok().body(userService.update(userId, request));
    }
}
