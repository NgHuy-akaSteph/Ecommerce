package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.response.UserResponse;
import com.myapp.ecommerce.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {

    UserService userService;


    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable("userId") String userId) {
        return userService.getUserById(userId);
    }
}
