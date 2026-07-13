package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.request.CartRequest;
import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.dto.response.CartResponse;
import com.myapp.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Tag(name = "Cart", description = "Shopping cart operations")
public class CartController {

    CartService cartService;

    @GetMapping
    ResponseEntity<ApiResponse<CartResponse>> getCartByUser() {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Get cart by user successfully", cartService.getCartByUser()));
    }

    @PostMapping("/add")
    ResponseEntity<ApiResponse<CartResponse>> addProductToCart(@RequestBody @Valid CartRequest request) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Add product to cart successfully", cartService.handleAddProductToCart(request)));
    }

    @PostMapping("/change")
    ResponseEntity<ApiResponse<CartResponse>> changeProductQuantityInCart(@RequestBody @Valid CartRequest request) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Change product quantity in cart successfully", cartService.handleChangeQuantityInCart(request)));
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<ApiResponse<CartResponse>> deleteProductFromCart(@PathVariable("id") UUID id) {
        return ResponseEntity.ok()
                .body(ApiResponse.ok("Delete cart detail from cart successfully", cartService.handleRemoveCartDetail(id)));
    }
}