package com.myapp.ecommerce.service.impl;

import com.myapp.ecommerce.dto.request.UserCreationRequest;
import com.myapp.ecommerce.dto.request.UserUpdateRequest;
import com.myapp.ecommerce.dto.response.UserResponse;
import com.myapp.ecommerce.dto.response.ApiPagination;
import com.myapp.ecommerce.entity.CartDetail;
import com.myapp.ecommerce.entity.Order;
import com.myapp.ecommerce.entity.Role;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.mapper.UserMapper;
import com.myapp.ecommerce.repository.*;
import com.myapp.ecommerce.service.RoleService;
import com.myapp.ecommerce.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    CartRepository cartRepository;
    CartDetailRepository cartDetailRepository;
    OrderRepository orderRepository;
    OrderDetailRepository orderDetailRepository;
    PasswordEncoder passwordEncoder;
    private final RoleService roleService;


    @Override
    public UserResponse createUser(UserCreationRequest request) {
        log.info("Create a new user");

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role role;
        if(request.getRole() == null){
            role = roleService.findByName("USER");
        }
        else {
            role = roleService.findById(request.getRole());
        }
        user.setRole(role);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        return userMapper.toUserResponse(user);
    }



    @Override
    public List<UserResponse> getAllUsers() {

        List<User> users = userRepository.findAll();
        return users.stream().map(userMapper::toUserResponse).toList();
    }

    @Override
    public ApiPagination<UserResponse> getAllUsers(Specification<User> spec, Pageable pageable) {
        log.info("Get all users");

        Page<User> pageUser = userRepository.findAll(spec, pageable);
        List<UserResponse> listUser = pageUser.getContent().stream().map(userMapper::toUserResponse).toList();

        //Build Meta class in ApiPagination
        ApiPagination.Meta metaAP = new ApiPagination.Meta();
        metaAP.setCurrent(pageable.getPageNumber() + 1);
        metaAP.setPageSize(pageable.getPageSize());
        metaAP.setPages(pageUser.getTotalPages());
        metaAP.setTotal(pageUser.getTotalElements());

        return ApiPagination.<UserResponse>builder()
                .meta(metaAP)
                .result(listUser)
                .build();
    }

    @Override
    public UserResponse getUserById(String userId) {
        log.info("Get details of user");

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getMyInfo() {
        log.info("Get my info");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @Override
    @Transactional
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        Hibernate.initialize(user.getRole());
        return user;
    }


    @Override
    public UserResponse update(String userId, UserUpdateRequest request) {
        log.info("Update a user");

        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.updateUser(user, request);
        // not done
        return null;
    }

    @Override
    public void delete(String userId) {
        log.info("Delete a user");

        //check if user existed
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        // delete cart
        if(user.getCart() != null){
            List<CartDetail> cartDetails = user.getCart().getCartDetails();
            if(!cartDetails.isEmpty()){
                cartDetailRepository.deleteAll(cartDetails);
            }
            cartRepository.delete(user.getCart());
        }
        //delete orders
        if(user.getOrders() != null) {
            List<Order> orders = user.getOrders();
            orders.forEach(order -> {
                if(order.getOrderDetails() != null){
                    orderDetailRepository.deleteAll(order.getOrderDetails());
                }
            });
            orderRepository.deleteAll(orders);
        }
        //delete user
        userRepository.delete(user);
    }

    @Override
    public boolean isExistByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public void updateUserToken(String refreshToken, String username) {
        User currentUser = getUserByUsername(username);
        if(currentUser != null){
            currentUser.setRefreshToken(refreshToken);
            userRepository.save(currentUser);
        }
    }

    @Override
    public void handleUserLogout(User user) {
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    @Override
    public User getUserByUsernameAndRefreshToken(String username, String refreshToken) {
        return userRepository.findByRefreshTokenAndUsername(refreshToken, username);
    }

}
