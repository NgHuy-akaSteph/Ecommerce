package com.myapp.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload for updating an existing user (partial update).")
public class UserUpdateRequest {

    @Schema(description = "New username (optional).")
    String username;

    @Schema(description = "New password (optional).")
    String password;

    @Schema(description = "Updated display name.")
    String name;

    @Schema(description = "Updated role id.")
    UUID role;
}
