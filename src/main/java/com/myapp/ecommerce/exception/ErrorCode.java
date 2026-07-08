package com.myapp.ecommerce.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    //AUTHENTICATION
    UNAUTHORIZED(401, "You do not have permission", HttpStatus.UNAUTHORIZED),
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(401, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    COOKIES_EMPTY(400, "You don't have refresh_token in cookies", HttpStatus.BAD_REQUEST),
    INVALID_REFRESH_TOKEN(400, "Invalid refresh_token", HttpStatus.BAD_REQUEST),
    INVALID_ACCESS_TOKEN(400, "Invalid access token", HttpStatus.BAD_REQUEST),
    BAD_CREDENTIALS(400, "Invalid username or password", HttpStatus.BAD_REQUEST),

    //CHECK EXISTED
    USER_EXISTED(400, "User existed.", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(404, "User not existed", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(404, "This category not found", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(400, "This category is existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(404, "This product not found", HttpStatus.NOT_FOUND),
    TAG_NOT_FOUND(404, "This tag not found", HttpStatus.NOT_FOUND),
    ORDER_NOT_FOUND(404, "This order not found", HttpStatus.NOT_FOUND),
    ROLE_NOT_EXISTED(400, "Role not existed", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_EXISTED(400, "Permission not existed", HttpStatus.BAD_REQUEST),
    CART_DETAIL_NOT_EXISTED(404, "Cart detail not existed", HttpStatus.NOT_FOUND),
    ORDER_ACCESS_DENIED(403, "You do not have permission to access this order", HttpStatus.FORBIDDEN),
    CART_DETAIL_ACCESS_DENIED(403, "You do not have permission to access this cart item", HttpStatus.FORBIDDEN),
    OUT_OF_STOCK(400, "Not enough stock available", HttpStatus.BAD_REQUEST),
    CONCURRENCY_ERROR(409, "Data was modified by another transaction. Please try again.", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS(400, "Email already exists", HttpStatus.BAD_REQUEST),
    INVALID_VERIFICATION_TOKEN(400, "Verification token is invalid or expired", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED(403, "Email not verified. Please check your inbox.", HttpStatus.FORBIDDEN),
    VERIFICATION_REQUIRED(403, "You need to verify your email or phone number to place an order", HttpStatus.FORBIDDEN),

    //ADDRESS
    ADDRESS_NOT_FOUND(404, "Address not found", HttpStatus.NOT_FOUND),
    ADDRESS_ACCESS_DENIED(403, "You do not have permission to access this address", HttpStatus.FORBIDDEN),

    //VALIDATE
    INVALID_KEY(400, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(400, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(400, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_ROLE_NAME(400, "Role name is invalid", HttpStatus.BAD_REQUEST),
    INVALID_PRICE_ORDER(400, "Price is invalid", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER(400, "Phone number is invalid", HttpStatus.BAD_REQUEST),
    INVALID_CATEGORY_NAME(400, "Category name is invalid", HttpStatus.BAD_REQUEST),
    INVALID_TAG_NAME(400, "Tag name is invalid", HttpStatus.BAD_REQUEST),
    TOO_MANY_REQUESTS(429, "Too many requests. Please try again later.", HttpStatus.TOO_MANY_REQUESTS),
    INVALID_FILE_TYPE(400, "Only image files are allowed. Supported formats: JPEG, PNG, WEBP, GIF.", HttpStatus.BAD_REQUEST),

    //VARIANT
    VARIANT_NOT_FOUND(404, "Variant not found", HttpStatus.NOT_FOUND),
    VARIANT_OPTION_NOT_FOUND(404, "Variant option not found", HttpStatus.NOT_FOUND),
    VARIANT_VALUE_NOT_FOUND(404, "Variant value not found", HttpStatus.NOT_FOUND),
    VARIANT_OPTION_EXISTED(400, "Variant option with this code already exists", HttpStatus.BAD_REQUEST),
    VARIANT_SKU_EXISTED(400, "Variant SKU already exists", HttpStatus.BAD_REQUEST),
    INVALID_SKU(400, "SKU is invalid", HttpStatus.BAD_REQUEST),
    ;


    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

}
