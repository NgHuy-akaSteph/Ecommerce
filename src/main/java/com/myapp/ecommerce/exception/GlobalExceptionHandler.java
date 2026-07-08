package com.myapp.ecommerce.exception;


import com.myapp.ecommerce.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<ApiResponse<?>> handlingRuntimeException(RuntimeException exception){
        log.error("Unhandled runtime exception", exception);

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ApiResponse<?>> handlingHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException exception) {
        log.warn("Method not supported: {}", exception.getMethod());

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage("HTTP method " + exception.getMethod() + " is not supported for this endpoint");

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse<?>> handlingAccessDeniedException(AccessDeniedException exception) {
        log.warn("Access denied: {}", exception.getMessage());

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(ErrorCode.UNAUTHORIZED.getCode());
        apiResponse.setMessage(ErrorCode.UNAUTHORIZED.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
    }

    @ExceptionHandler(value = DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse<?>> handlingDataIntegrityViolation(DataIntegrityViolationException exception) {
        log.error("Data integrity violation", exception);

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage("Data integrity constraint violated. Please check your input.");

        return ResponseEntity.status(HttpStatus.CONFLICT).body(apiResponse);
    }

    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    ResponseEntity<ApiResponse<?>> handlingTypeMismatch(MethodArgumentTypeMismatchException exception) {
        log.warn("Type mismatch for parameter '{}': {}", exception.getName(), exception.getValue());

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(ErrorCode.INVALID_KEY.getCode());
        apiResponse.setMessage("Invalid value for parameter '" + exception.getName() + "'");

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    ResponseEntity<ApiResponse<?>> handlingNoResourceFound(NoResourceFoundException exception) {
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(ErrorCode.INVALID_KEY.getCode());
        apiResponse.setMessage("Resource not found");

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
    }

    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    ResponseEntity<ApiResponse<?>> handlingMediaTypeNotSupported(HttpMediaTypeNotSupportedException exception) {
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(ErrorCode.INVALID_KEY.getCode());
        apiResponse.setMessage("Unsupported media type: " + exception.getContentType());

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(apiResponse);
    }

    @ExceptionHandler(value = ObjectOptimisticLockingFailureException.class)
    ResponseEntity<ApiResponse<?>> handlingOptimisticLocking(ObjectOptimisticLockingFailureException exception){
        ErrorCode errorCode = ErrorCode.CONCURRENCY_ERROR;

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = {
            UsernameNotFoundException.class,
            BadCredentialsException.class})
    ResponseEntity<ApiResponse<?>> handlingBadCredentialsException(BadCredentialsException exception) {

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(ErrorCode.BAD_CREDENTIALS.getCode());
        apiResponse.setMessage(ErrorCode.BAD_CREDENTIALS.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    // Replace "min" in message to
    private String mapAttribute(String message, Map<String, Object> attributes) {
        String MIN_ATTRIBUTE = "min";
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));

        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<?>> handlingValidation(MethodArgumentNotValidException exception) {
        var allErrors = exception.getBindingResult().getAllErrors();
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 0; i < allErrors.size(); i++) {
            var error = allErrors.get(i);
            String enumKey = error.getDefaultMessage();
            if (enumKey != null && !enumKey.startsWith("must match")) {
                boolean isValidKey = java.util.Arrays.stream(ErrorCode.values())
                        .anyMatch(e -> e.name().equals(enumKey));
                if (isValidKey) {
                    ErrorCode code = ErrorCode.valueOf(enumKey);
                    var violation = error.unwrap(ConstraintViolation.class);
                    Map<String, Object> attributes = violation.getConstraintDescriptor().getAttributes();
                    messageBuilder.append(mapAttribute(code.getMessage(), attributes));
                } else {
                    messageBuilder.append(enumKey);
                }
            } else {
                messageBuilder.append("Invalid field value");
            }
            if (i < allErrors.size() - 1) {
                messageBuilder.append("; ");
            }
        }

        String message = messageBuilder.length() > 0 ? messageBuilder.toString() : ErrorCode.INVALID_KEY.getMessage();

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setStatusCode(ErrorCode.INVALID_KEY.getCode());
        apiResponse.setMessage(message);

        return ResponseEntity.badRequest().body(apiResponse);
    }
}
