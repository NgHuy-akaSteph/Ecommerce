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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Request payload for creating a variant option type (e.g., Size, Color).")
public class VariantOptionRequest {

    @Schema(description = "Option name, e.g. 'Size', 'Color'.")
    @NotBlank(message = "INVALID_OPTION_NAME")
    @Size(min = 1, max = 100, message = "INVALID_OPTION_NAME")
    String name;

    @Schema(description = "Unique code identifier, e.g. 'size', 'color'.")
    @NotBlank(message = "INVALID_OPTION_CODE")
    @Size(min = 1, max = 50, message = "INVALID_OPTION_CODE")
    String code;
}
