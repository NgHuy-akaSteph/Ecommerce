package com.myapp.ecommerce.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request payload for creating or updating a permission.")
public class PermissionRequest {

    @Schema(description = "Permission display name.")
    String name;

    @Schema(description = "API path guarded by this permission (e.g. /users).")
    String apiPath;

    @Schema(description = "HTTP method guarded (GET, POST, PUT, DELETE).")
    String method;

    @Schema(description = "Logical module grouping (e.g. USER, ORDER).")
    String module;

    @Schema(description = "Whether the permission is active.")
    boolean active;

}
