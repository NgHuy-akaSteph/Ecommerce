package com.myapp.ecommerce.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    //AUTHENTICATION
    UNCATEGORIZED_EXCEPTION(500, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),


    //CHECK EXISTED
    USER_EXISTED(400, "User existed.", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(404, "User not existed", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(400, "This category not found", HttpStatus.BAD_REQUEST),
    CATEGORY_EXISTED(400, "This category is existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(400, "This product not found", HttpStatus.BAD_REQUEST),
    TAG_NOT_FOUND(400, "This tag not found", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(400, "This order not found", HttpStatus.BAD_REQUEST),
    ROLE_NOT_EXISTED(400, "Role not existed", HttpStatus.BAD_REQUEST),
    PERMISSION_NOT_EXISTED(400, "Permission not existed", HttpStatus.BAD_REQUEST),
    CART_DETAIL_NOT_EXISTED(400, "Cart detail not existed", HttpStatus.BAD_REQUEST),

    //VALIDATE
    INVALID_KEY(400, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(400, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(400, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_ROLE_NAME(400, "Role name is invalid", HttpStatus.BAD_REQUEST),
    INVALID_PRICE_ORDER(400, "Price is invalid", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER(400, "Phone number is invalid", HttpStatus.BAD_REQUEST),
    INVALID_CATEGORY_NAME(400, "Category name is invalid", HttpStatus.BAD_REQUEST),
    INVALID_TAG_NAME(400, "Tag name is invalid", HttpStatus.BAD_REQUEST),
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
