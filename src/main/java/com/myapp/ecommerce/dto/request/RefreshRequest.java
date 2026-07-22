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
@Schema(description = "Request payload for refreshing an access token using a refresh token.")
public class RefreshRequest {

    @Schema(description = "Refresh token used to obtain a new access token.")
    String token;
}
