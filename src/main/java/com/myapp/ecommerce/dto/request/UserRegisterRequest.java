package com.myapp.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request payload for registering a new user account.")
public class UserRegisterRequest {

    @Schema(description = "Desired username; must be at least 4 characters.")
    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    @Schema(description = "Account password; must be at least 6 characters.")
    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    @Schema(description = "Full display name.")
    @NotBlank
    String name;

    @Schema(description = "Valid email address used for verification and notifications.")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email;
}
