package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.request.CartRequest;
import com.myapp.ecommerce.dto.response.CartResponse;
import com.myapp.ecommerce.service.CartService;
import com.myapp.ecommerce.util.annotation.ApiMessage;
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

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartController {

    CartService cartService;

    @GetMapping
    @ApiMessage("Get cart by user successfully")
    ResponseEntity<CartResponse> getCartByUser(){
        return ResponseEntity.ok().body(cartService.getCartByUser());
    }

    @PostMapping("/add")
    @ApiMessage("Add product to cart successfully")
    ResponseEntity<CartResponse> addProductToCart(@RequestBody @Valid CartRequest request){
        return ResponseEntity.ok().body(cartService.handleAddProductToCart(request));
    }

    @PostMapping("/change")
    @ApiMessage("Change product quantity in cart successfully")
    ResponseEntity<CartResponse> changeProductQuantityInCart(@RequestBody @Valid CartRequest request){
        return ResponseEntity.ok().body(cartService.handleChangeQuantityInCart(request));
    }

    @DeleteMapping("/delete/{id}")
    @ApiMessage("Delete cart detail from cart successfully")
    ResponseEntity<CartResponse> deleteProductFromCart(@PathVariable("id") String id){
        return ResponseEntity.ok().body(cartService.handleRemoveCartDetail(id));
    }
}
