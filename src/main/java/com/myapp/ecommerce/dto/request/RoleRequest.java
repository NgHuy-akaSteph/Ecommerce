package com.myapp.ecommerce.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request payload for creating or updating a role.")
public class RoleRequest {

    @Schema(description = "Role name; must be at least 3 characters.")
    @Size(min = 3, message="INVALID_ROLE_NAME")
    String name;

    @Schema(description = "Optional description for the role.")
    String description;

    @Schema(description = "Whether the role is active and assignable.")
    boolean active;

    @Schema(description = "List of permission ids granted by this role.")
    List<String> perIds; // List of permission ids

}
