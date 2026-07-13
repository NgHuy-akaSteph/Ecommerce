package com.myapp.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request payload for creating a new user (admin operation).")
public class UserCreationRequest {

    @Schema(description = "Desired username; must be at least 4 characters.")
    @Size(min = 4, message = "USERNAME_INVALID")
    String username;

    @Schema(description = "Account password; must be at least 6 characters.")
    @Size(min = 6, message = "INVALID_PASSWORD")
    String password;

    @Schema(description = "Full display name.")
    @NotBlank
    String name;

    @Schema(description = "Role id to assign to the user.")
    UUID role;
}
