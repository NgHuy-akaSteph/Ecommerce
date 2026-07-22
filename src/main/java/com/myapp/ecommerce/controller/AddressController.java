package com.myapp.ecommerce.controller;

import com.myapp.ecommerce.dto.request.AddressRequest;
import com.myapp.ecommerce.dto.response.AddressResponse;
import com.myapp.ecommerce.dto.response.ApiResponse;
import com.myapp.ecommerce.entity.User;
import com.myapp.ecommerce.exception.AppException;
import com.myapp.ecommerce.exception.ErrorCode;
import com.myapp.ecommerce.repository.UserRepository;
import com.myapp.ecommerce.service.AddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Addresses", description = "Manage user shipping/billing addresses")
public class AddressController {

    AddressService addressService;
    UserRepository userRepository;

    @GetMapping
    ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses() {
        return ResponseEntity.ok(
                ApiResponse.ok("Get my addresses successfully",
                        addressService.getMyAddresses(currentUserId()))
        );
    }

    @GetMapping("/{id}")
    ResponseEntity<ApiResponse<AddressResponse>> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Get address successfully",
                        addressService.getById(id, currentUserId()))
        );
    }

    @PostMapping
    ResponseEntity<ApiResponse<AddressResponse>> create(@Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Create address successfully",
                        addressService.create(currentUserId(), request)));
    }

    @PutMapping("/{id}")
    ResponseEntity<ApiResponse<AddressResponse>> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(
                ApiResponse.ok("Update address successfully",
                        addressService.update(id, currentUserId(), request)));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") UUID id) {
        addressService.delete(id, currentUserId());
        return ResponseEntity.ok(ApiResponse.ok("Delete address successfully", null));
    }

    @PutMapping("/{id}/default")
    ResponseEntity<ApiResponse<AddressResponse>> setDefault(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(
                ApiResponse.ok("Set default address successfully",
                        addressService.setDefault(id, currentUserId())));
    }

    private UUID currentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return user.getId();
    }
}
