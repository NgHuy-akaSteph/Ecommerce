package com.myapp.ecommerce.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request payload for logging out and revoking an access token.")
public class LogoutRequest {

    @Schema(description = "Access token to revoke.")
    String token;
}
